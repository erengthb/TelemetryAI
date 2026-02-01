#pragma once

#include "CoreMinimal.h"
#include "Containers/Ticker.h"
#include "Runtime/Launch/Resources/Version.h"
#include "TelemetryAISettings.h"

#if ENGINE_MAJOR_VERSION > 5 || (ENGINE_MAJOR_VERSION == 5 && ENGINE_MINOR_VERSION >= 7)
using FTelemetryAITickerHandle = FTSTicker::FDelegateHandle;
#else
using FTelemetryAITickerHandle = FDelegateHandle;
#endif

struct FTelemetryEvent
{
    FString EventId;
    int64 TimestampClient = 0;
    FString EventName;
    FString SessionId;
    FString PlayerId;
    FString PropertiesJson;
    FString SourceFile;
};

class TELEMETRYAI_API FTelemetryAIClient
{
public:
    static FTelemetryAIClient& Get();

    void Init(const FTelemetryAIConfig& InConfig);
    void Shutdown();

    void TrackEvent(const FString& EventName, const FString& PropertiesJson);
    void TrackEventSimple(const FString& EventName, const TMap<FString, FString>& Properties);

    void Flush();

    void SetPlayerId(const FString& NewPlayerId);
    void SetSessionId(const FString& NewSessionId);

    bool IsInitialized() const;

private:
    FTelemetryAIClient() = default;

    bool Tick(float DeltaTime);
    void SendBatch(const TArray<FTelemetryEvent>& Batch, const FString& Body, const TArray<uint8>& Compressed, bool bCompressed);
    TArray<FTelemetryEvent> DequeueBatch(int32 MaxCount);
    FString SerializeEventJson(const FTelemetryEvent& Event) const;
    bool ParseEventJson(const FString& Json, FTelemetryEvent& OutEvent) const;
    void RequeueBatch(const TArray<FTelemetryEvent>& Batch);

    FString BuildBatchUrl() const;

    void EnsureSpoolDirs();
    void LoadSpool();
    bool HasSpoolPending() const;
    void AppendSpool(const TArray<FTelemetryEvent>& Events);
    FString GetSpoolRootPath() const;
    FString GetSpoolPendingPath() const;
    FString GetSpoolProcessingPath() const;
    FString CreateSpoolFilePath();

    FTelemetryAIConfig Config;
    TArray<FTelemetryEvent> Queue;
    FCriticalSection QueueMutex;
    FString PlayerId;
    FString SessionId;

    bool bInitialized = false;
    double NextSendTimeSec = 0.0;
    double BackoffSec = 0.0;
    double LastFlushTimeSec = 0.0;
    bool bSendInFlight = false;

    FString CurrentSpoolFile;
    int32 CurrentSpoolLineCount = 0;
    TMap<FString, int32> SpoolRemaining;

    FTelemetryAITickerHandle TickerHandle;
};

namespace TelemetryAI
{
    inline void Event(const FString& EventName)
    {
        FTelemetryAIClient::Get().TrackEvent(EventName, FString());
    }

    inline void EventJson(const FString& EventName, const FString& PropertiesJson)
    {
        FTelemetryAIClient::Get().TrackEvent(EventName, PropertiesJson);
    }
}
