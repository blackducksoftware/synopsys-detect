# Inspectors

An inspector is typically a plugin that a [company_name] [solution_name] detector uses to access the internal resources of a package manager through its API.

There are currently three inspectors that [company_name] [solution_name] might download, and one internally coded inspector.

If [company_name] [solution_name] decides that your package manager requires an external inspector you must either be online or have the applicable air gap files available.

## Gradle inspector

For Gradle, a common integrations library is added as a dependency to a temporary Gradle script file that is executed by the [company_name] [solution_name] Gradle detector. 

If you are online, the Synopsys Artifactory instance is added as a Maven repository and the library is downloaded by Gradle.
If you are offline, the air gap library jar files are added as classpath file dependencies.

In both cases, the [company_name] [solution_name] Gradle detector executes the custom Gradle script mentioned above, which invokes a custom Gradle task.

The source code for the library is located at [GitHub](https://github.com/blackducksoftware/integration-common).

## Docker Inspector

The Docker Inspector is available as a Java jar file or shell script for Linux or Mac.

If you are online, the Synopsys Artifactory instance is used to download the Docker Inspector jar file.

If you are offline, the Docker Inspector jar file and required Docker image tar files are sourced from the path provided to the Docker Inspector air gap files.
[company_name] [solution_name] loads the Docker images (container-based services that Docker Inspector depends on) from the provided image tar files so they are available to the Docker Inspector.

In either case the Docker Inspector jar is run by default which uses your Docker engine to start and stop the container-based services.

The source code for Docker Inspector is located at [GitHub](https://github.com/blackducksoftware/detect-docker-inspector).

## NuGet Inspector

The NuGet inspector is available as an independent executable for Windows, Linux, and Mac.

If you are online, the Synopsys Artifactory instance is used to download the applicable NuGet inspector which is then unzipped and the runtime files are located.
If you are offline, the air gap inspector runtime files are located at the provided path.

In either case the located executable is run which communicates with NuGet and the dotnet build system.

The source code for the NuGet inspector is located at [GitHub](https://github.com/blackducksoftware/detect-nuget-inspector).

## Python Inspector

While Python has an inspector this inspector is not downloaded from an external source and is contained in the [company_name] [solution_name] source code.

The Python Inspector is a Python script that [company_name] [solution_name] executes using Python.

This script uses pip's internal methods to extract dependencies.
