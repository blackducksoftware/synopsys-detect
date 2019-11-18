# Project and version naming

The project and version names of the project to which ${solution_name} writes results are, by default, derived from the project on which ${solution_name} is run.  The mechanism ${solution_name} uses to determine the project and version names depends on project type. If ${solution_name} cannot determine the project and version names, then ${solution_name} will use the project directory name as the project name, and the value "Default Detect Version" as the version name.

You can use the following properties to override the project and version names:
```
--detect.project.name=PROJECT-NAME
--detect.project.version.name=VERSION-NAME
```
You can use the following property to change the default version to a timestamp.
```
--detect.default.project.version.scheme=timestamp
```
You can use the following property to customize the timestamp format:
```
--detect.default.project.version.timeformat=’yyyy-MM-dd:HH:mm:ss.SSS’
```
## Project and version naming for Git projects

If no package manager provides project and version names, you have not provided the project and version names through properties, and the project uses Git, ${solution_name} attempts to use Git to determine project information
Project information is extracted from the remote URL for the current branch. The version is the current branch name.  This is performed by the Git detector. If you do not want ${solution_name} to use Git data, you can omit the Git detector using the following property:
```
--detect.excluded.detector.types=GIT
```

For example, for a project with a remote URL of "https://github.com/blackducksoftware/synopsys-detect" and a checked-out branch "5.5.0",
${solution_name} will (by default) use project name "blackducksoftware/synopsys-detect" and project version "5.5.0".

${solution_name} attempts to derive project and version information by running the Git executable. If that is not successful, it will attempt to derive
project and version information by parsing Git files.

In ${solution_name} versions 5.5.0 and higher, there is a new ${solution_name} property for providing the
path to the Git executable: detect.git.path.
