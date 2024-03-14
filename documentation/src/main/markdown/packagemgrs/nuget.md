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

<note type="note">

* NuGet Project Inspector relies on Project Inspector thus does not accept NuGet specific configuration properties.   
* The NuGet Detectors do not work with mono.
</note>

## Excluding dependency types
[company_name] [solution_name] offers the ability to exclude package manager specific dependency types from the BOM.
Nuget dependency types can be filtered with the [detect.nuget.dependency.types.excluded](../properties/detectors/nuget.md#nuget-dependency-types-excluded) property.
This property supports exclusion of dependencies in projects that use PackageReference, and packages.config for listing dependencies.
<note type="note">Support for storing dependencies in Json files has been deprecated by Nuget. As such we will not be enhancing the properties to exclude dependency types in this manner.</note>

A project might be using a dependency purely as a development harness and you might not want to expose that to projects that will consume the package. You can use the PrivateAssets metadata to control this behavior. [company_name] [solution_name] looks for the PrivateAssets attribute used within PackageReference tags to identify a development dependency. [company_name] [solution_name] will ignore the contents of the tag and only observe the presence of these PrivateAssets to exclude those development related dependencies.
For packages.config file, [company_name] [solution_name] will look for developmentDependency tags to determine whether to include or exclude a dependency.

### [company_name] [solution_name] NuGet Inspector downloads

[company_name] [solution_name] jar execution will automatically download any required binaries not located in the cache.

For direct access to the binaries or source code see [download locations](../downloadingandinstalling/downloadlocations.md).

### Inspector Operation

An inspector is self-contained and requires no installation. Each executable is platform dependent and the correct inspector is downloaded by [company_name] [solution_name] at runtime.

NuGet Solution Native Inspector runs if one or more solution (.sln) files are found and derives packages (dependencies) via analysis of solution files. Central Package Management is supported to include any package versions and global package references mentioned under `Directory.Packages.props` files indicated the (.sln) file for each project under the solution. Any package references and versions in the solution's `Directory.Build.props` will be included for each project under the solution.

<note type="tip">When running the NuGet Solution Native Inspector the `--detect.detector.search.depth=` value is ignored if a solution (.sln) file is found that contains project references that include subdirectories at levels lower than the specified search depth.
</note>

NuGet Project Native Inspector runs if no solution (.sln) files are found, and one or more project files are found. NuGet Project Native Inspector derives packages (dependencies) from project (.csproj, .fsproj, etc.) file content.

NuGet inspectors derive dependency information from solution (.sln) files in this order:
1. Directory.Packages.props
2. packages.config
3. project.lock.json
4. project.assets.json
5. project.json
6. XML of the project file

In addition to the packages and dependencies found from the above files, packages and dependencies will be included from other `project.assets.json` files if configured in the corresponding project's property file. (`<projectname>.<projectfiletype>.nuget.g.props`).

After discovering dependencies, NuGet client libraries are used to collect further information about the dependencies and write them to a JSON file (`<projectname>_inspection.json`). [company_name] [solution_name] then parses that file for the dependency information.

### NuGet Project Native Inspector supported project files

| Azure Stream Analytics | Cloud Computing | Common Project System Files | C# | Deployment | Docker Compose | F# |
|---|---|---|---|---|---|---|
| *.asaproj | *.ccproj | *.msbuildproj | *.csproj | *.deployproj | *.dcproj | *.fsproj |

| Fabric Application | Hive | JavaScript | .NET Core | Node.js | Pig | Python |
|---|---|---|---|---|---|---|
| *.sfproj | *.hiveproj | *.jsproj | *.xproj | *.njsproj | *.pigproj | *.pyproj |

| RStudio | Shared Projects | SQL | SQL Project Files | U-SQL | VB | VC++ |
|---|---|---|---|---|---|---|
| *.rproj | *.shproj | *.sqlproj | *.dbproj | *.usqlproj | *.vbproj | *.vcxproj *.vcproj |

### NuGet Detector buildless mode

In buildless mode, [company_name] [solution_name] uses Project Inspector to find dependencies and only supports `.csproj` and `.sln` files.

### [company_name] [solution_name] NuGet Inspector on Alpine

The [company_name] [solution_name] NuGet Inspectors depend on packages not installed by default on Alpine systems, such as the dynamic loader for DLLs.

When the dynamic loader is not present, an error message similar to the following appears in the log as a result of
[company_name] [solution_name]'s attempt to execute the NuGet Inspector:
```
java.io.IOException: Cannot run program ".../tools/detect-nuget-inspector/detect-nuget-inspector-1.0.1-linux/detect-nuget-inspector" (in directory ...): error=2, No such file or directory
```

To add these packages to an Alpine system:
```
apk add libstdc++ gcompat icu
```
