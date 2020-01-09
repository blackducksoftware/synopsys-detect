# NuGet support

The NuGet detectors can discover dependencies of NuGet projects.

## Overview

There are two Nuget detectors: the NuGet solution detector, and the NuGet project detector. Each detector runs one of two available inspectors: the newer dotnet inspector, and the older classic, or exe, inspector.

On non-Windows systems: Only the dotnet inspector is supported. A dotnet executable is required.

On Windows systems: Both inspectors (dotnet and classic) are supported. A dotnet executable is not required. If no dotnet executable is found, ${solution_name} runs the classic inspector.

By default, ${solution_name} looks for dotnet on $PATH. You can override this by setting the dotnet path property to point to your dotnet executable.

The NuGet detectors do not work with mono.

### Dotnet inspector

Source: https://github.com/blackducksoftware/blackduck-nuget-inspector

Binary: https://repo.blackducksoftware.com:443/artifactory/bds-integrations-nuget-release/BlackduckNugetInspector

Requires: .NET framework version 4.5

### Classic inspector

Source: https://github.com/blackducksoftware/integration-nuget-inspector

Binary: https://repo.blackducksoftware.com:443/artifactory/bds-integrations-nuget-release/IntegrationNugetInspector

## Operation

Each inspector (dotnet and classic) supports two detectors: NuGet solution detector, and NuGet project detector.

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

The NuGet detectors run the appropriate inspector (dotnet: BlackduckNugetInspector, or classic: IntegrationNugetInspector), which it normally downloads automatically.

The NuGet inspectors derive dependency information from the first type of file in this order:
1. packages.config
2. project.lock.json
3. project.assets.json
4. project.json
5. XML of the project file

After discovering dependencies, both the dotnet inspector (BlackduckNugetInspector) and the classic inspector (IntegrationNugetInspector) use NuGet client libraries to collect further information about the dependencies and write it to a JSON file (`<projectname>_inspection.json`). ${solution_name} then parses that file for the dependency information.
