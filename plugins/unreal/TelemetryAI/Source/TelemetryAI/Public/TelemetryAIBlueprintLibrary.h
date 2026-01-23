#pragma once

#include "Kismet/BlueprintFunctionLibrary.h"
#include "TelemetryAISettings.h"
#include "TelemetryAIBlueprintLibrary.generated.h"

UCLASS()
class TELEMETRYAI_API UTelemetryAIBlueprintLibrary : public UBlueprintFunctionLibrary
{
    GENERATED_BODY()

public:
    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void InitFromSettings();

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void Init(const FTelemetryAIConfig& Config);

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void Shutdown();

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void TrackEventJson(const FString& EventName, const FString& PropertiesJson);

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void TrackEventSimple(const FString& EventName, const TMap<FString, FString>& Properties);

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void Flush();

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void SetPlayerId(const FString& PlayerId);

    UFUNCTION(BlueprintCallable, Category = "TelemetryAI")
    static void SetSessionId(const FString& SessionId);
};
