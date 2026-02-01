#pragma once

#include "CoreMinimal.h"
#include "TelemetryAIEventNames.generated.h"

UENUM(BlueprintType)
enum class ETelemetryAIEventName : uint16
{
    Unknown UMETA(DisplayName = "unknown")
};

namespace TelemetryAIEventNames
{
    inline constexpr const TCHAR* Unknown = TEXT("unknown");
}

namespace TelemetryAIEvents
{
    inline const TCHAR* ToString(ETelemetryAIEventName Name)
    {
        switch (Name)
        {
            default:
                return TEXT("");
        }
    }
}
