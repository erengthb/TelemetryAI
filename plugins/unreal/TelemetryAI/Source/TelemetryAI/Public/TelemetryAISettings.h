#pragma once

#include "CoreMinimal.h"
#include "Engine/DeveloperSettings.h"
#include "TelemetryAISettings.generated.h"

USTRUCT(BlueprintType)
struct FTelemetryAIConfig
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    FString EndpointUrl = "http://localhost:8080";

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    FString ApiKey = "";

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    FString Environment = "dev";

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    FString BuildVersion = "0.0.1";

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    int32 FlushIntervalSec = 5;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    int32 MaxQueueSize = 5000;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    int32 MaxBatchSize = 200;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    bool bEnableGzip = false;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    bool bEnableSpool = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "TelemetryAI")
    FString SpoolPath = "";
};

UCLASS(config = Game, defaultconfig, meta = (DisplayName = "TelemetryAI"))
class TELEMETRYAI_API UTelemetryAISettings : public UDeveloperSettings
{
    GENERATED_BODY()

public:
    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    FString EndpointUrl = "http://localhost:8080";

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    FString ApiKey = "";

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    FString Environment = "dev";

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    FString BuildVersion = "0.0.1";

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    int32 FlushIntervalSec = 5;

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    int32 MaxQueueSize = 5000;

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    int32 MaxBatchSize = 200;

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    bool bEnableGzip = false;

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    bool bEnableSpool = true;

    UPROPERTY(EditAnywhere, config, Category = "TelemetryAI")
    FString SpoolPath = "";

    UPROPERTY(VisibleAnywhere, config, Category = "TelemetryAI|Schema")
    int32 ActiveSchemaVersion = 0;

    UPROPERTY(VisibleAnywhere, config, Category = "TelemetryAI|Schema")
    TArray<FString> ActiveSchemaEventNames;

    UPROPERTY(VisibleAnywhere, config, Category = "TelemetryAI|Schema", meta = (MultiLine = "true"))
    FString GeneratedEventHeaderPreview;

    UPROPERTY(VisibleAnywhere, config, Category = "TelemetryAI|Schema")
    FString LastSchemaSyncAt;

    UPROPERTY(VisibleAnywhere, config, Category = "TelemetryAI|Schema")
    FString LastSchemaSyncError;

    FTelemetryAIConfig ToConfig() const;
};
