using UnrealBuildTool;

public class TelemetryAI : ModuleRules
{
    public TelemetryAI(ReadOnlyTargetRules Target) : base(Target)
    {
        PCHUsage = PCHUsageMode.UseExplicitOrSharedPCHs;

        PublicDependencyModuleNames.AddRange(
            new string[]
            {
                "Core",
                "CoreUObject",
                "Engine",
                "HTTP",
                "Json",
                "JsonUtilities",
                "DeveloperSettings"
            }
        );

        PrivateDependencyModuleNames.AddRange(new string[] { });
    }
}
