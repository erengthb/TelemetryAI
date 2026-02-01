#include "TelemetryAIBlueprintLibrary.h"
#include "TelemetryAIClient.h"
#include "TelemetryAISettings.h"

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
