#include "TelemetryAI.h"
#include "TelemetryAISchemaSync.h"
#include "Modules/ModuleManager.h"
#include "HAL/IConsoleManager.h"

IMPLEMENT_MODULE(FTelemetryAIModule, TelemetryAI)

void FTelemetryAIModule::StartupModule()
{
    static FAutoConsoleCommand SyncSchemaCommand(
        TEXT("TelemetryAI.SyncSchema"),
        TEXT("Fetch active schema with X-Api-Key and generate event names header."),
        FConsoleCommandWithArgsDelegate::CreateLambda([](const TArray<FString>& Args)
        {
            const bool bWriteHeader = !Args.Contains(TEXT("nowrite"));
            FTelemetryAISchemaSync::SyncFromSettings(bWriteHeader);
        })
    );
}

void FTelemetryAIModule::ShutdownModule()
{
}
