# Gradle support

${solution_name} has two detectors for Gradle:

* [Gradle inspector detector](#gradleinspectordetector)
* [Gradle parse detector](#gradleparsedetector)

<a name="gradleinspectordetector"></a>
# Gradle inspector detector

The Gradle inspector detector discovers dependencies of Gradle projects.

The Gradle inspector detector attempts to run on your project if it finds a build.gradle file in the source directory (top level).

The Gradle inspector detector also requires either gradlew or gradle:

1. ${solution_name} looks for gradlew in the source directory (top level). You can override this by setting the Gradle path property. If not overridden and not found:
1. ${solution_name} looks for gradle on $PATH.

The Gradle inspector detector runs `gradlew gatherDependencies` to get a list of the project's dependencies, and then parses the output.

The inspector defines the custom task 'gatherDependencies' with the help of a Gradle script (`init-detect.gradle`), which it usually downloads automatically. The file init-detect.gradle has a dependencies on ExcludedIncludedFilter, ExcludedIncludedWildcardFilter, and IntegrationEscapeUtil that come from https://github.com/blackducksoftware/integration-common.
Filtering (including/excluding projects and configurations) is performed by the Gradle/Groovy code to control the output of the `dependencies` Gradle task invoked by the 'gradlew gatherDependencies' command.

The init-detect.gradle script configures each project with the custom 'gatherDependencies' task, which will invoke the 'dependencies' Gradle task on each project. This ensures the same output as previous versions is produced. The inspector consumes the output of `gradlew gatherDependencies` task.

<a name="gradleinspectorwithproxy"></a>
###Running the Gradle inspector with a proxy
${solution_name} will pass along supplied [proxy host](../../../properties/configuration/proxy/#proxy-host-advanced) and [proxy port](../../../properties/configuration/proxy/#proxy-port-advanced) properties to the Gradle daemon if applicable.
<a name="gradleinspectorwithproxy"></a>

<a name="gradleparsedetector"></a>
# Gradle parse detector

TBD
