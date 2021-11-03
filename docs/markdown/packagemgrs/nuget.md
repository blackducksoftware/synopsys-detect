# NuGet support

The NuGet detectors can discover dependencies of NuGet projects.

## Overview

There are two Nuget detectors: the NuGet solution detector, and the NuGet project detector. Each detector runs one of three available inspectors: the new DotNet 5 inspector, DotNet 3.1 inspector, the DotNet inspector (for DotNet 2.1 runtime), or the old Classic inspector (aka exe inspector).

On non-Windows systems: The DotNet 5, DotNet 3.1 and DotNet inspectors are supported. A dotnet executable is required.

On Windows systems: All inspectors (DotNet 5, DotNet 3.1, DotNet, and Classic) are supported. A dotnet executable is not required. If no dotnet executable is found, [solution_name] runs the Classic inspector.

By default, [solution_name] looks for dotnet on $PATH. You can override this by setting the dotnet path property to point to your dotnet executable.

The NuGet detectors do not work with mono.

### DotNet 5 inspector

Source: https://github.com/blackducksoftware/nuget-dotnet5-inspector

Binary: https://sig-repo.synopsys.com/bds-integrations-nuget-release/NugetDotnet5Inspector/

Runtime: DotNet 5

### DotNet 3.1 inspector

Source: https://github.com/blackducksoftware/nuget-dotnet3-inspector

Binary: https://sig-repo.synopsys.com/bds-integrations-nuget-release/NugetDotnet3Inspector/

Runtime: DotNet 3.1

Requires: .NET framework version 4.5

### DotNet inspector

Source: https://github.com/blackducksoftware/blackduck-nuget-inspector

Binary: https://sig-repo.synopsys.com/bds-integrations-nuget-release/BlackduckNugetInspector/

Runtime: DotNet 2.1

Requires: .NET framework version 4.5

### Classic inspector

Source: https://github.com/blackducksoftware/integration-nuget-inspector

Binary: https://sig-repo.synopsys.com/bds-integrations-nuget-release/IntegrationNugetInspector/

## Operation

Each inspector (DotNet 5, DotNet 3.1, DotNet, and Classic) supports two detectors: NuGet solution detector, and NuGet project detector.

The NuGet solution detector derives packages (dependencies) from solution (.sln) files.

The Nuget project detector derives packages (dependencies) from project (.csproj, .fsproj, etc.) files. The supported project files are:
````
// C#
"*.csproj",
// F#
"*.fsproj",
// VB
"*.vbproj",
// Azure Stream Analytics
"*.asaproj",
// Docker Compose
"*.dcproj",
// Shared Projects
"*.shproj",
// Cloud Computing
"*.ccproj",
// Fabric Application
"*.sfproj",
// Node.js
"*.njsproj",
// VC++
"*.vcxproj",
// VC++
"*.vcproj",
// .NET Core
"*.xproj",
// Python
"*.pyproj",
// Hive
"*.hiveproj",
// Pig
"*.pigproj",
// JavaScript
"*.jsproj",
// U-SQL
"*.usqlproj",
// Deployment
"*.deployproj",
// Common Project System Files
"*.msbuildproj",
// SQL
"*.sqlproj",
// SQL Project Files
"*.dbproj",
// RStudio
"*.rproj"
````

The NuGet solution detector runs if one or more solution (.sln) files are found.

The NuGet Project detector runs if no solution files are found, and one or more project files are found.  Refer to the preceding list of project file types.

The NuGet detectors run the appropriate inspector (dotnet 5: NugetDotnet3Inspector, dotnet 3.1: NugetDotnet3Inspector, dotnet 2.1: BlackduckNugetInspector, or classic: IntegrationNugetInspector), which it normally downloads automatically.

The NuGet inspectors derive dependency information from the first type of file in this order:
1. packages.config
2. project.lock.json
3. project.assets.json
4. project.json
5. XML of the project file

After discovering dependencies, the dotnet inspector (NugetDotnet3Inspector or BlackduckNugetInspector) and the classic inspector (IntegrationNugetInspector) use NuGet client libraries to collect further information about the dependencies and write it to a JSON file (`<projectname>_inspection.json`). [solution_name] then parses that file for the dependency information.

# NuGet parse detector

The buildless NuGet detector uses Project Inspector to find dependencies.

It only supports ".csproj" and ".sln" files.