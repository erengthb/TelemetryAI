#include "TelemetryAISettings.h"

FTelemetryAIConfig UTelemetryAISettings::ToConfig() const
{
    FTelemetryAIConfig Config;
    Config.EndpointUrl = EndpointUrl;
    Config.ApiKey = ApiKey;
    Config.Environment = Environment;
    Config.BuildVersion = BuildVersion;
    Config.FlushIntervalSec = FlushIntervalSec;
    Config.MaxQueueSize = MaxQueueSize;
    Config.MaxBatchSize = MaxBatchSize;
    Config.bEnableGzip = bEnableGzip;
    Config.bEnableSpool = bEnableSpool;
    Config.SpoolPath = SpoolPath;
    return Config;
}
