#include "TelemetryAIClient.h"
#include "HttpModule.h"
#include "Interfaces/IHttpResponse.h"
#include "Dom/JsonObject.h"
#include "Serialization/JsonSerializer.h"
#include "Serialization/JsonWriter.h"
#include "Misc/DateTime.h"
#include "Misc/Guid.h"
#include "Misc/Paths.h"
#include "Misc/FileHelper.h"
#include "HAL/FileManager.h"
#include "Containers/Ticker.h"
#include "Misc/EngineVersion.h"
#include "GenericPlatform/GenericPlatformMisc.h"
#include "GenericPlatform/GenericPlatformProperties.h"
#include "Async/Async.h"
#include "Misc/Compression.h"

DEFINE_LOG_CATEGORY_STATIC(LogTelemetryAI, Log, All);

FTelemetryAIClient& FTelemetryAIClient::Get()
{
    static FTelemetryAIClient Instance;
    return Instance;
}

void FTelemetryAIClient::Init(const FTelemetryAIConfig& InConfig)
{
    Config = InConfig;
    if (Config.EndpointUrl.EndsWith("/"))
    {
        Config.EndpointUrl.LeftChopInline(1);
    }

    if (Config.FlushIntervalSec <= 0)
    {
        Config.FlushIntervalSec = 5;
    }

    if (Config.MaxBatchSize <= 0)
    {
        Config.MaxBatchSize = 200;
    }

    if (Config.MaxQueueSize <= 0)
    {
        Config.MaxQueueSize = 5000;
    }

    EnsureSpoolDirs();
    LoadSpool();

    FString MaskedKey = Config.ApiKey;
    if (MaskedKey.IsEmpty())
    {
        MaskedKey = TEXT("(empty)");
    }
    else if (MaskedKey.Len() > 4)
    {
        MaskedKey = FString::Printf(TEXT("****%s"), *MaskedKey.Right(4));
    }

    if (!TickerHandle.IsValid())
    {
        TickerHandle = FTSTicker::GetCoreTicker().AddTicker(
            FTickerDelegate::CreateRaw(this, &FTelemetryAIClient::Tick),
            1.0f
        );
    }

    bInitialized = true;
    LastFlushTimeSec = FPlatformTime::Seconds();
    UE_LOG(LogTelemetryAI, Log, TEXT("TelemetryAI initialized. endpoint=%s env=%s apiKey=%s"), *Config.EndpointUrl, *Config.Environment, *MaskedKey);
}

void FTelemetryAIClient::Shutdown()
{
    if (!bInitialized)
    {
        return;
    }

    {
        FScopeLock Lock(&QueueMutex);
        if (Queue.Num() > 0 && Config.bEnableSpool)
        {
            AppendSpool(Queue);
            Queue.Reset();
        }
    }

    if (TickerHandle.IsValid())
    {
        FTSTicker::GetCoreTicker().RemoveTicker(TickerHandle);
        TickerHandle.Reset();
    }

    bInitialized = false;
    UE_LOG(LogTelemetryAI, Log, TEXT("TelemetryAI shutdown."));
}

void FTelemetryAIClient::TrackEvent(const FString& EventName, const FString& PropertiesJson)
{
    if (!bInitialized)
    {
        return;
    }

    if (EventName.IsEmpty())
    {
        return;
    }

    FTelemetryEvent Event;
    Event.EventId = FGuid::NewGuid().ToString(EGuidFormats::DigitsWithHyphens);
    const FDateTime Now = FDateTime::UtcNow();
    Event.TimestampClient = Now.ToUnixTimestamp() * 1000 + Now.GetMillisecond();
    Event.EventName = EventName;

    if (SessionId.IsEmpty())
    {
        SessionId = FGuid::NewGuid().ToString(EGuidFormats::DigitsWithHyphens);
    }
    Event.SessionId = SessionId;
    Event.PlayerId = PlayerId;
    Event.PropertiesJson = PropertiesJson;

    {
        FScopeLock Lock(&QueueMutex);
        if (Queue.Num() >= Config.MaxQueueSize)
        {
            UE_LOG(LogTelemetryAI, Warning, TEXT("Queue full, dropping event."));
            return;
        }
        Queue.Add(MoveTemp(Event));
    }
}

void FTelemetryAIClient::TrackEventSimple(const FString& EventName, const TMap<FString, FString>& Properties)
{
    TSharedPtr<FJsonObject> Obj = MakeShared<FJsonObject>();
    for (const TPair<FString, FString>& Pair : Properties)
    {
        Obj->SetStringField(Pair.Key, Pair.Value);
    }

    FString Json;
    TSharedRef<TJsonWriter<>> Writer = TJsonWriterFactory<>::Create(&Json);
    FJsonSerializer::Serialize(Obj.ToSharedRef(), Writer);
    TrackEvent(EventName, Json);
}

void FTelemetryAIClient::Flush()
{
    if (!bInitialized)
    {
        return;
    }

    const double Now = FPlatformTime::Seconds();
    if (Now < NextSendTimeSec)
    {
        return;
    }

    if (bSendInFlight)
    {
        return;
    }

    TArray<FTelemetryEvent> Batch = DequeueBatch(Config.MaxBatchSize);
    if (Batch.Num() == 0)
    {
        return;
    }

    bSendInFlight = true;
    Async(EAsyncExecution::ThreadPool, [this, Batch]()
    {
        TSharedPtr<FJsonObject> Root = MakeShared<FJsonObject>();
        TSharedPtr<FJsonObject> Client = MakeShared<FJsonObject>();
        Client->SetStringField(TEXT("sdk"), TEXT("telemetryai-unreal"));
        Client->SetStringField(TEXT("sdkVersion"), TEXT("0.1.0"));
        Client->SetStringField(TEXT("engine"), TEXT("unreal"));
        Client->SetStringField(TEXT("engineVersion"), FEngineVersion::Current().ToString());
        Client->SetStringField(TEXT("buildVersion"), Config.BuildVersion);
        Client->SetStringField(TEXT("platform"), ANSI_TO_TCHAR(FPlatformProperties::PlatformName()));
        Root->SetObjectField(TEXT("client"), Client);

        TArray<TSharedPtr<FJsonValue>> Events;
        Events.Reserve(Batch.Num());
        for (const FTelemetryEvent& Event : Batch)
        {
            TSharedPtr<FJsonObject> EventObj = MakeShared<FJsonObject>();
            EventObj->SetStringField(TEXT("eventId"), Event.EventId);
            EventObj->SetNumberField(TEXT("timestampClient"), static_cast<double>(Event.TimestampClient));
            EventObj->SetStringField(TEXT("eventName"), Event.EventName);
            EventObj->SetStringField(TEXT("sessionId"), Event.SessionId);
            if (!Event.PlayerId.IsEmpty())
            {
                EventObj->SetStringField(TEXT("playerId"), Event.PlayerId);
            }

            if (!Event.PropertiesJson.IsEmpty())
            {
                TSharedPtr<FJsonObject> Props;
                const TSharedRef<TJsonReader<>> Reader = TJsonReaderFactory<>::Create(Event.PropertiesJson);
                if (FJsonSerializer::Deserialize(Reader, Props) && Props.IsValid())
                {
                    EventObj->SetObjectField(TEXT("properties"), Props);
                }
            }

            Events.Add(MakeShared<FJsonValueObject>(EventObj));
        }
        Root->SetArrayField(TEXT("events"), Events);

        FString Body;
        TSharedRef<TJsonWriter<>> Writer = TJsonWriterFactory<>::Create(&Body);
        FJsonSerializer::Serialize(Root.ToSharedRef(), Writer);

        TArray<uint8> Compressed;
        bool bCompressed = false;
        if (Config.bEnableGzip && Body.Len() > 0)
        {
            FTCHARToUTF8 Utf8(*Body);
            const int32 SourceSize = Utf8.Length();
            if (SourceSize > 0)
            {
                const FName GzipFormat(TEXT("Gzip"));
                const int32 Bound = FCompression::CompressMemoryBound(GzipFormat, SourceSize);
                Compressed.SetNumUninitialized(Bound);
                int32 CompressedSize = Bound;
                if (FCompression::CompressMemory(GzipFormat, Compressed.GetData(), CompressedSize, Utf8.Get(), SourceSize))
                {
                    Compressed.SetNum(CompressedSize);
                    bCompressed = true;
                }
                else
                {
                    Compressed.Reset();
                }
            }
        }

        AsyncTask(ENamedThreads::GameThread, [this, Batch, Body, Compressed, bCompressed]()
        {
            if (!bInitialized)
            {
                bSendInFlight = false;
                return;
            }
            if (Body.IsEmpty())
            {
                bSendInFlight = false;
                return;
            }
            SendBatch(Batch, Body, Compressed, bCompressed);
        });
    });
}

void FTelemetryAIClient::SetPlayerId(const FString& NewPlayerId)
{
    PlayerId = NewPlayerId;
}

void FTelemetryAIClient::SetSessionId(const FString& NewSessionId)
{
    SessionId = NewSessionId;
}

bool FTelemetryAIClient::IsInitialized() const
{
    return bInitialized;
}

bool FTelemetryAIClient::Tick(float DeltaTime)
{
    if (!bInitialized)
    {
        return true;
    }

    const double Now = FPlatformTime::Seconds();
    if ((Now - LastFlushTimeSec) >= Config.FlushIntervalSec)
    {
        LastFlushTimeSec = Now;
        bool bQueueEmpty = false;
        {
            FScopeLock Lock(&QueueMutex);
            bQueueEmpty = Queue.Num() == 0;
        }
        if (bQueueEmpty && HasSpoolPending())
        {
            LoadSpool();
        }
        Flush();
    }

    return true;
}

TArray<FTelemetryEvent> FTelemetryAIClient::DequeueBatch(int32 MaxCount)
{
    TArray<FTelemetryEvent> Batch;
    FScopeLock Lock(&QueueMutex);
    const int32 Count = FMath::Min(MaxCount, Queue.Num());
    if (Count <= 0)
    {
        return Batch;
    }
    Batch.Reserve(Count);
    Batch.Append(Queue.GetData(), Count);
    Queue.RemoveAt(0, Count);
    return Batch;
}

void FTelemetryAIClient::SendBatch(const TArray<FTelemetryEvent>& Batch, const FString& Body, const TArray<uint8>& Compressed, bool bCompressed)
{
    FHttpModule& Http = FHttpModule::Get();
    TSharedRef<IHttpRequest, ESPMode::ThreadSafe> Request = Http.CreateRequest();
    Request->SetURL(BuildBatchUrl());
    Request->SetVerb("POST");
    Request->SetHeader("Content-Type", "application/json");
    Request->SetHeader("X-Api-Key", Config.ApiKey);
    if (bCompressed && Compressed.Num() > 0)
    {
        Request->SetHeader("Content-Encoding", "gzip");
        Request->SetContent(Compressed);
    }
    else
    {
        Request->SetContentAsString(Body);
    }

    Request->OnProcessRequestComplete().BindLambda(
        [this, Batch](FHttpRequestPtr Req, FHttpResponsePtr Response, bool bSuccess)
        {
            const int32 Code = Response.IsValid() ? Response->GetResponseCode() : 0;
            if (Code == 401 || Code == 403)
            {
                UE_LOG(LogTelemetryAI, Error, TEXT("Auth failed (%d). Check API key."), Code);
                bSendInFlight = false;
                return;
            }

            const bool ShouldRetry = !bSuccess || Code == 0 || Code == 429 || Code >= 500;
            if (ShouldRetry)
            {
                RequeueBatch(Batch);
                BackoffSec = BackoffSec <= 0.0 ? 1.0 : FMath::Min(BackoffSec * 2.0, 60.0);
                NextSendTimeSec = FPlatformTime::Seconds() + BackoffSec;
                UE_LOG(LogTelemetryAI, Warning, TEXT("Batch send failed (%d). Backoff %.1fs"), Code, BackoffSec);
                bSendInFlight = false;
                return;
            }

            BackoffSec = 0.0;
            NextSendTimeSec = 0.0;

            for (const FTelemetryEvent& Event : Batch)
            {
                if (!Event.SourceFile.IsEmpty())
                {
                    if (int32* RemainingPtr = SpoolRemaining.Find(Event.SourceFile))
                    {
                        *RemainingPtr = FMath::Max(*RemainingPtr - 1, 0);
                        if (*RemainingPtr == 0)
                        {
                            IFileManager::Get().Delete(*Event.SourceFile);
                            SpoolRemaining.Remove(Event.SourceFile);
                        }
                    }
                }
            }

            bSendInFlight = false;
        }
    );

    if (!Request->ProcessRequest())
    {
        RequeueBatch(Batch);
        bSendInFlight = false;
    }
}

FString FTelemetryAIClient::SerializeEventJson(const FTelemetryEvent& Event) const
{
    TSharedPtr<FJsonObject> EventObj = MakeShared<FJsonObject>();
    EventObj->SetStringField(TEXT("eventId"), Event.EventId);
    EventObj->SetNumberField(TEXT("timestampClient"), static_cast<double>(Event.TimestampClient));
    EventObj->SetStringField(TEXT("eventName"), Event.EventName);
    EventObj->SetStringField(TEXT("sessionId"), Event.SessionId);
    if (!Event.PlayerId.IsEmpty())
    {
        EventObj->SetStringField(TEXT("playerId"), Event.PlayerId);
    }

    if (!Event.PropertiesJson.IsEmpty())
    {
        TSharedPtr<FJsonObject> Props;
        const TSharedRef<TJsonReader<>> Reader = TJsonReaderFactory<>::Create(Event.PropertiesJson);
        if (FJsonSerializer::Deserialize(Reader, Props) && Props.IsValid())
        {
            EventObj->SetObjectField(TEXT("properties"), Props);
        }
    }

    FString Json;
    TSharedRef<TJsonWriter<>> Writer = TJsonWriterFactory<>::Create(&Json);
    FJsonSerializer::Serialize(EventObj.ToSharedRef(), Writer);
    return Json;
}

bool FTelemetryAIClient::ParseEventJson(const FString& Json, FTelemetryEvent& OutEvent) const
{
    TSharedPtr<FJsonObject> Obj;
    const TSharedRef<TJsonReader<>> Reader = TJsonReaderFactory<>::Create(Json);
    if (!FJsonSerializer::Deserialize(Reader, Obj) || !Obj.IsValid())
    {
        return false;
    }

    OutEvent.EventId = Obj->GetStringField(TEXT("eventId"));
    OutEvent.TimestampClient = static_cast<int64>(Obj->GetNumberField(TEXT("timestampClient")));
    OutEvent.EventName = Obj->GetStringField(TEXT("eventName"));
    OutEvent.SessionId = Obj->GetStringField(TEXT("sessionId"));
    if (Obj->HasField(TEXT("playerId")))
    {
        OutEvent.PlayerId = Obj->GetStringField(TEXT("playerId"));
    }

    if (Obj->HasField(TEXT("properties")))
    {
        TSharedPtr<FJsonObject> Props = Obj->GetObjectField(TEXT("properties"));
        if (Props.IsValid())
        {
            FString PropsJson;
            TSharedRef<TJsonWriter<>> Writer = TJsonWriterFactory<>::Create(&PropsJson);
            FJsonSerializer::Serialize(Props.ToSharedRef(), Writer);
            OutEvent.PropertiesJson = PropsJson;
        }
    }
    return true;
}

void FTelemetryAIClient::RequeueBatch(const TArray<FTelemetryEvent>& Batch)
{
    if (Batch.Num() == 0)
    {
        return;
    }

    TArray<FTelemetryEvent> PendingSpool;
    {
        FScopeLock Lock(&QueueMutex);
        int32 Space = Config.MaxQueueSize - Queue.Num();
        for (int32 Index = Batch.Num() - 1; Index >= 0; --Index)
        {
            const FTelemetryEvent& Event = Batch[Index];
            if (Space > 0)
            {
                Queue.Insert(Event, 0);
                Space--;
            }
            else if (Event.SourceFile.IsEmpty())
            {
                PendingSpool.Add(Event);
            }
        }
    }

    if (PendingSpool.Num() > 0 && Config.bEnableSpool)
    {
        AppendSpool(PendingSpool);
    }
}

FString FTelemetryAIClient::BuildBatchUrl() const
{
    FString Url = Config.EndpointUrl;
    if (!Url.EndsWith("/"))
    {
        Url += "/";
    }
    Url += "v1/events/batch";
    return Url;
}

void FTelemetryAIClient::EnsureSpoolDirs()
{
    if (!Config.bEnableSpool)
    {
        return;
    }

    IFileManager::Get().MakeDirectory(*GetSpoolPendingPath(), true);
    IFileManager::Get().MakeDirectory(*GetSpoolProcessingPath(), true);
}

bool FTelemetryAIClient::HasSpoolPending() const
{
    if (!Config.bEnableSpool)
    {
        return false;
    }

    TArray<FString> Files;
    IFileManager::Get().FindFiles(Files, *GetSpoolPendingPath(), TEXT("*.jsonl"));
    return Files.Num() > 0;
}

void FTelemetryAIClient::LoadSpool()
{
    if (!Config.bEnableSpool)
    {
        return;
    }

    if (SpoolRemaining.Num() > 0)
    {
        return;
    }

    TArray<FString> Files;
    IFileManager::Get().FindFiles(Files, *GetSpoolPendingPath(), TEXT("*.jsonl"));
    Files.Sort();

    for (const FString& File : Files)
    {
        const FString PendingPath = GetSpoolPendingPath() / File;
        const FString ProcessingPath = GetSpoolProcessingPath() / File;
        IFileManager::Get().Move(*ProcessingPath, *PendingPath);
        if (CurrentSpoolFile == PendingPath)
        {
            CurrentSpoolFile.Reset();
            CurrentSpoolLineCount = 0;
        }

        TArray<FString> Lines;
        if (!FFileHelper::LoadFileToStringArray(Lines, *ProcessingPath))
        {
            continue;
        }

        if (Lines.Num() > Config.MaxQueueSize)
        {
            UE_LOG(LogTelemetryAI, Warning, TEXT("Spool file too large for queue. Skipping: %s"), *ProcessingPath);
            IFileManager::Get().Move(*PendingPath, *ProcessingPath);
            continue;
        }

        {
            FScopeLock Lock(&QueueMutex);
            if (Queue.Num() + Lines.Num() > Config.MaxQueueSize)
            {
                IFileManager::Get().Move(*PendingPath, *ProcessingPath);
                continue;
            }
        }

        int32 LoadedCount = 0;
        for (const FString& Line : Lines)
        {
            FTelemetryEvent Event;
            if (ParseEventJson(Line, Event))
            {
                Event.SourceFile = ProcessingPath;
                FScopeLock Lock(&QueueMutex);
                Queue.Add(MoveTemp(Event));
                LoadedCount++;
            }
        }

        if (LoadedCount > 0)
        {
            SpoolRemaining.Add(ProcessingPath, LoadedCount);
        }

        break;
    }
}

void FTelemetryAIClient::AppendSpool(const TArray<FTelemetryEvent>& Events)
{
    if (!Config.bEnableSpool)
    {
        return;
    }

    const int32 MaxSpoolLines = FMath::Max(1, Config.MaxQueueSize);
    if (CurrentSpoolFile.IsEmpty() || CurrentSpoolLineCount >= MaxSpoolLines)
    {
        CurrentSpoolFile = CreateSpoolFilePath();
        CurrentSpoolLineCount = 0;
    }

    for (const FTelemetryEvent& Event : Events)
    {
        const FString Line = SerializeEventJson(Event) + "\n";
        FFileHelper::SaveStringToFile(
            Line,
            *CurrentSpoolFile,
            FFileHelper::EEncodingOptions::AutoDetect,
            &IFileManager::Get(),
            FILEWRITE_Append
        );
        CurrentSpoolLineCount++;
    }
}

FString FTelemetryAIClient::GetSpoolPendingPath() const
{
    return GetSpoolRootPath() / "pending";
}

FString FTelemetryAIClient::GetSpoolProcessingPath() const
{
    return GetSpoolRootPath() / "processing";
}

FString FTelemetryAIClient::GetSpoolRootPath() const
{
    if (!Config.SpoolPath.IsEmpty())
    {
        return Config.SpoolPath;
    }
    return FPaths::ProjectSavedDir() / "TelemetryAI" / "spool";
}

FString FTelemetryAIClient::CreateSpoolFilePath()
{
    const FString Stamp = FDateTime::UtcNow().ToString(TEXT("%Y%m%d_%H%M%S"));
    const FString Filename = FString::Printf(TEXT("spool_%s_%04d.jsonl"), *Stamp, FMath::RandRange(1, 9999));
    return GetSpoolPendingPath() / Filename;
}
