# Gradle support

## Related properties

[Detector properties](../properties/detectors/gradle.md)

<note type="Note">Gradle Project Inspector relies on the Project Inspector tool thus does not accept Gradle specific configuration properties.
</note>

## Overview

[solution_name] has two detectors for Gradle:

* Gradle Native Inspector
* Gradle Project Inspector

## Gradle Native Inspector

* Discovers dependencies of Gradle projects.

* Will run on your project if it finds a build.gradle file in the top level source directory.

Gradle Native Inspector requires either gradlew or gradle:

1. [solution_name] looks for gradlew in the source directory (top level). You can override this by setting the Gradle path property. If not overridden and not found:
1. [solution_name] looks for gradle on $PATH.

Runs `gradlew gatherDependencies` to get a list of the project's dependencies, and then parses the output.

Gradle Native Inspector allows you to filter projects based on both the name and the path. The path is unique for each project in the hierarchy and follows the form ":parent:child". Both filtering mechanism support wildcards.

The inspector defines the custom task 'gatherDependencies' with the help of a Gradle script (`init-detect.gradle`), which it usually downloads automatically. The file init-detect.gradle has a dependencies on ExcludedIncludedFilter,
ExcludedIncludedWildcardFilter, and IntegrationEscapeUtil that come from https://github.com/blackducksoftware/integration-common. Filtering (including/excluding projects and configurations) is performed by the Gradle/Groovy code to control
the output of the `dependencies` Gradle task invoked by the 'gradlew gatherDependencies' command.

The init-detect.gradle script configures each project with the custom 'gatherDependencies' task, which invokes the 'dependencies' Gradle task on each project. This ensures the same output is produced as previous versions. The inspector consumes the output of `gradlew gatherDependencies` task.

### Running the Gradle inspector with a proxy

[solution_name] will pass along supplied [proxy host](../properties/configuration/proxy.md#proxy-host-advanced) and [proxy port](../properties/configuration/proxy.md#proxy-port-advanced) properties to the Gradle daemon if applicable.

### Gradle detector buildless

The buildless gradle detector uses Project Inspector to find dependencies and does not support dependency exclusions.

It currently supports "build.gradle" and does not support Kotlin build files.
