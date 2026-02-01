#include "TelemetryAIBlueprintLibrary.h"
#include "TelemetryAIClient.h"
#include "TelemetryAISettings.h"
#include "TelemetryAISchemaSync.h"

void UTelemetryAIBlueprintLibrary::InitFromSettings()
{
    const UTelemetryAISettings* Settings = GetDefault<UTelemetryAISettings>();
    if (!Settings)
    {
        return;
    }
    FTelemetryAIClient::Get().Init(Settings->ToConfig());
}

void UTelemetryAIBlueprintLibrary::Init(const FTelemetryAIConfig& Config)
{
    FTelemetryAIClient::Get().Init(Config);
}

void UTelemetryAIBlueprintLibrary::Shutdown()
{
    FTelemetryAIClient::Get().Shutdown();
}

void UTelemetryAIBlueprintLibrary::TrackEventJson(const FString& EventName, const FString& PropertiesJson)
{
    FTelemetryAIClient::Get().TrackEvent(EventName, PropertiesJson);
}

void UTelemetryAIBlueprintLibrary::TrackEvent(const FString& EventName)
{
    FTelemetryAIClient::Get().TrackEvent(EventName, FString());
}

void UTelemetryAIBlueprintLibrary::TrackEventSimple(const FString& EventName, const TMap<FString, FString>& Properties)
{
    FTelemetryAIClient::Get().TrackEventSimple(EventName, Properties);
}

void UTelemetryAIBlueprintLibrary::TrackEventEnum(ETelemetryAIEventName EventName)
{
    const TCHAR* Name = TelemetryAIEvents::ToString(EventName);
    if (Name && *Name)
    {
        FTelemetryAIClient::Get().TrackEvent(FString(Name), FString());
    }
}

void UTelemetryAIBlueprintLibrary::TrackEventEnumJson(ETelemetryAIEventName EventName, const FString& PropertiesJson)
{
    const TCHAR* Name = TelemetryAIEvents::ToString(EventName);
    if (Name && *Name)
    {
        FTelemetryAIClient::Get().TrackEvent(FString(Name), PropertiesJson);
    }
}

void UTelemetryAIBlueprintLibrary::Flush()
{
    FTelemetryAIClient::Get().Flush();
}

void UTelemetryAIBlueprintLibrary::SetPlayerId(const FString& PlayerId)
{
    FTelemetryAIClient::Get().SetPlayerId(PlayerId);
}

void UTelemetryAIBlueprintLibrary::SetSessionId(const FString& SessionId)
{
    FTelemetryAIClient::Get().SetSessionId(SessionId);
}

bool UTelemetryAIBlueprintLibrary::SyncSchema(bool bWriteHeader)
{
    return FTelemetryAISchemaSync::SyncFromSettings(bWriteHeader);
}
