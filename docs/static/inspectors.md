# Inspectors

Inspectors are typically plugins that detect uses to access the internal resources of a package manager.

There are currently three inspectors that detect might download and one inspector it has internally. 
  
If Detect decides that your package manager needs an external inspector, you must either be online or have the applicable Air Gap files. 

## Gradle Inspector

The Gradle inspector is added as a dependency to a temporary gradle script file. 

If you are online, the Synopsys Artifactory is added as a maven repository and the inspector is downloaded by Gradle.
If you are offline, the air gap inspector jar files are added as classpath file dependencies.

In both cases, a custom gradle script is then run which invokes the Gradle inspector's task.  

The source code for the gradle inspector is located on [GitHub](https://github.com/blackducksoftware/integration-gradle-inspector).

## Docker Inspector

The Docker inspector is available as an java jar or shell script for Linux or Mac. 

If you are online, the Synopsys Artifactory is used to download the docker inspector jar file. 
If you are offline, the docker inspector jar as well as all needed docker image tars are located at the provided path.
Additionally, if offline, the inspector jar will be provided and will automatically import all of the found image tars.  

In both cases, the located inspector jar is run which communicates with the docker installed on your system.

The source code for the docker inspector is located on [GitHub](https://github.com/blackducksoftware/blackduck-docker-inspector).

## NuGet Inspector

The NuGet inspector is available as an independent executable for Windows or as a dotnet application for all dotnet supported operating systems. 

If you are online, the Synopsys Artifactory is used to download the applicable NuGet package which is then unzipped and the runtime files are located.
If you are offline, the air gap inspector runtime files are simply located at the provided path.

In both cases, the located executable is run which communicates with NuGet and the dotnet build system. 

The source code for the NuGet Executable inspector is located on [GitHub](https://github.com/blackducksoftware/integration-nuget-inspector).
The source code for the NuGet dotnet inspector is located on [GitHub](https://github.com/blackducksoftware/blackduck-nuget-inspector).

## Python Inspector

While Python has an inspector, this inspector is not downloaded from an external source and is contained in the detect source code.

The Python Inspector is simply a python script that detect executes using python. 

This script uses pip's internal methods to extract dependencies.