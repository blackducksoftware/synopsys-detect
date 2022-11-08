# NuGet support

## Related properties

[Detector properties](../properties/detectors/nuget.md)

## Overview

The NuGet detectors can discover dependencies of NuGet projects.

There are two NuGet detectors: the NuGet solution detector, and the NuGet project detector. Both detectors run the Detect NuGet Inspector, a platform dependent self-contained executable. The currently supported platforms are Windows, Linux and Mac.

The NuGet detectors do not work with mono.

### Detect NuGet Inspector

Source: https://github.com/blackducksoftware/detect-nuget-inspector

Binary: https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/detect-nuget-inspector/

#### Detect NuGet Inspector on alpine

The Detect NuGet Inspector depends on packages not installed by default on alpine systems, such as the dynamic loader for DLLs.

When the dynamic loader is not present, an error message similar to the following appears in the log as a result of
[solution_name]'s attempt to execute the NuGet Inspector:
```
java.io.IOException: Cannot run program ".../tools/detect-nuget-inspector/detect-nuget-inspector-1.0.1-linux/detect-nuget-inspector" (in directory ...): error=2, No such file or directory
```

To add these packages to an alpine system:
```
apk add libstdc++ gcompat icu
```

## Operation

The inspector is fully self-contained and requires no installation. Each executable is platform dependent. The correct inspector is downloaded by detect at runtime.

The NuGet solution detector derives packages (dependencies) from solution (.sln) files.

The NuGet project detector derives packages (dependencies) from project (.csproj, .fsproj, etc.) files. The supported project files are:
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

The NuGet inspectors derive dependency information from the first type of file in this order:
1. packages.config
2. project.lock.json
3. project.assets.json
4. project.json
5. XML of the project file

After discovering dependencies, the inspector uses NuGet client libraries to collect further information about the dependencies and write it to a JSON file (`<projectname>_inspection.json`). [solution_name] then parses that file for the dependency information.

# NuGet parse detector

The buildless NuGet detector uses Project Inspector to find dependencies.

It only supports ".csproj" and ".sln" files.
