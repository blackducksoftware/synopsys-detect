# NuGet support

## Related properties

[Detector properties](../properties/detectors/nuget.md)

## Overview

The NuGet detectors can discover dependencies of NuGet projects.

There are three NuGet detectors: the NuGet Solution Native Inspector, NuGet Project Native Inspector, and the NuGet Project Inspector. The detectors run a platform dependent self-contained executable that is currently supported on Windows, Linux and Mac platforms.

<note type="Note">

* NuGet Project Inspector relies on Project Inspector thus does not accept NuGet specific configuration properties.   
* The NuGet Detectors do not work with mono.
</note>

### [solution_name] NuGet Inspector

Source: https://github.com/blackducksoftware/detect-nuget-inspector

Binary: https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/detect-nuget-inspector/

#### [solution_name] NuGet Inspector on Alpine

The [solution_name] NuGet Inspectors depend on packages not installed by default on Alpine systems, such as the dynamic loader for DLLs.

When the dynamic loader is not present, an error message similar to the following appears in the log as a result of
[solution_name]'s attempt to execute the NuGet Inspector:
```
java.io.IOException: Cannot run program ".../tools/detect-nuget-inspector/detect-nuget-inspector-1.0.1-linux/detect-nuget-inspector" (in directory ...): error=2, No such file or directory
```

To add these packages to an Alpine system:
```
apk add libstdc++ gcompat icu
```

## Operation

An inspector is fully self-contained and requires no installation. Each executable is platform dependent and the correct inspector is downloaded by [solution_name] at runtime.

The NuGet Solution Native Inspector derives packages (dependencies) from solution (.sln) files.

The NuGet Project Native Inspector derives packages (dependencies) from project (.csproj, .fsproj, etc.) files. The supported project files are:
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

The NuGet Solution Native Inspector runs if one or more solution (.sln) files are found.

The NuGet Project Native Inspector runs if no solution files are found, and one or more project files are found.  Refer to the preceding list of project file types.

The NuGet inspectors derive dependency information from the first type of file in this order:
1. packages.config
2. project.lock.json
3. project.assets.json
4. project.json
5. XML of the project file

After discovering dependencies, NuGet client libraries are used to collect further information about the dependencies and write it to a JSON file (`<projectname>_inspection.json`). [solution_name] then parses that file for the dependency information.

### NuGet detector buildless

In buildless mode, [solution_name] uses Project Inspector to find dependencies and only supports ".csproj" and ".sln" files.
