# Project, Version, and Code Location Naming

The following sections describe the project, version, and code location (scan) naming in [solution_name].

## Project and version naming

The project and version names of the project to which [solution_name] writes results are, by default, derived from the project on which [solution_name] is run.  The mechanism [solution_name] uses to determine the project and version names depends on project type. If [solution_name] cannot determine the project and version names, then [solution_name] uses the project directory name as the project name, and the value "Default Detect Version" as the version name.

You can use the following properties to override the project and version names:
```
--detect.project.name=PROJECT-NAME
--detect.project.version.name=VERSION-NAME
```
You can use the following property to change the default version to a timestamp:
```
--detect.default.project.version.scheme=timestamp
```
You can use the following property to customize the timestamp format:
```
--detect.default.project.version.timeformat='yyyy-MM-dd:HH:mm:ss.SSS'
```
## Project and version naming for Git projects

If no package manager provides project and version names, you have not provided the project and version names through properties, and the project uses Git, [solution_name] attempts to use Git to determine project information.

Project information is extracted from the remote URL for the current branch. The version is the current branch name, or the commit hash if a detached head is checked out.  This is done by the Git detector. If you don't want [solution_name] to use Git data, omit the Git detector using the following property:
```
--detect.excluded.detector.types=GIT
```

For example, for a project with a remote URL of "https://github.com/blackducksoftware/synopsys-detect" and a checked-out branch of "5.5.0",
[solution_name] by default uses the project name "blackducksoftware/synopsys-detect" and project version "5.5.0".

[solution_name] attempts to derive project and version information by running the Git executable. If that is not successful, it attempts to derive
project and version information by parsing Git files.

In [solution_name] versions 5.5.0 and higher, there is a new [solution_name] property for providing the
path to the Git executable: detect.git.path.

## Code location (scan) naming

[solution_name] often generates multiple code locations (scans) in a single run.
Each code location name consists of a base name and a type suffix that indicates what type of scan generated it.

| Scan type | Default base name |Suffix |
|---|---|---|
| Package manager (all detectors plus Docker Inspector and Bazel) | project/version | bdio |
| Impact analysis | directory/project/version | impact |
| Signature | directory/project/version | signature |
| Binary | file/project/version | binary |
| IaC | directory/project/version | iac |

You can modify the base name by adding a prefix and/or a suffix to the default base name using the `detect.project.codelocation.prefix`
and `detect.project.codelocation.suffix` properties.

You also have the option to set the base name using the `detect.code.location.name` property.
When `detect.code.location.name` is set, `detect.project.codelocation.prefix` 
and `detect.project.codelocation.suffix` are ignored.
