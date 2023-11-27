# NuGet support

## Related properties

[Detector properties](../properties/detectors/nuget.md)

## Overview

The NuGet detectors are used to discover dependencies of NuGet projects.

There are three NuGet detectors: 
 * NuGet Solution Native Inspector
 * NuGet Project Native Inspector
 * NuGet Project Inspector

The detectors run a platform dependent self-contained executable that is currently supported on Windows, Linux, and Mac platforms.

<note type="Note">

* NuGet Project Inspector relies on Project Inspector thus does not accept NuGet specific configuration properties.   
* The NuGet Detectors do not work with mono.
</note>

### [solution_name] NuGet Inspector downloads

[Binary files](https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/detect-nuget-inspector/)

[Source code](https://github.com/blackducksoftware/detect-nuget-inspector)

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

## Inspector Operation

An inspector is self-contained and requires no installation. Each executable is platform dependent and the correct inspector is downloaded by [solution_name] at runtime.

NuGet Solution Native Inspector runs if one or more solution (.sln) files are found and derives packages (dependencies) via analysis of solution files. Central Package Management is supported to include any package versions and global package references mentioned under Directory.Packages.props files indicated the (.sln) file for each project under the solution. Any package references and versions in the solution's Directory.Build.props will be included for each project under the solution.

NuGet Project Native Inspector runs if no solution (.sln) files are found, and one or more project files are found. NuGet Project Native Inspector derives packages (dependencies) from project (.csproj, .fsproj, etc.) file content.

NuGet inspectors derive dependency information from solution (.sln) files in this order:
1. Directory.Packages.props
2. packages.config
3. project.lock.json
4. project.assets.json
5. project.json
6. XML of the project file

In addition to the packages and dependencies found from the above files, packages and dependencies will be included from other `project.assets.json` files if configured in the corresponding project's property file. (`<projectname>.<projectfiletype>.nuget.g.props`).

After discovering dependencies, NuGet client libraries are used to collect further information about the dependencies and write them to a JSON file (`<projectname>_inspection.json`). [solution_name] then parses that file for the dependency information.

### NuGet Project Native Inspector supported project files
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

### NuGet Detector buildless mode

In buildless mode, [solution_name] uses Project Inspector to find dependencies and only supports `.csproj` and `.sln` files.
