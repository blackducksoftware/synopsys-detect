# Gradle support

Detect has two detectors for Gradle:

* [Gradle Inspector Detector](#gradleinspectordetector)
* [Gradle Parse Detector](#gradleparsedetector)

<a name="gradleinspectordetector"></a>
# Gradle Inspector Detector

The Gradle Inspector Detector can discover dependencies of Gradle projects.

The Gradle Inspector Detector will attempt to run on your project if it finds a build.gradle file in the source directory (top level).

The Gradle Inspector Detector also requires either gradlew or gradle:

1. Detect looks for gradlew in the source directory (top level). You can override this by setting the gradle path property. If not overridden and not found:
1. Detect looks for gradle on $PATH.

The Gradle Inspector Detector runs `gradlew dependencies` to get a list of the project's dependencies, and then parses the output.

It consumes the output of `gradlew dependencies` with the help of a gradle script (`init-detect.gradle`), which it usually downloads automatically. init-detect.gradle has a dependency DependencyGatherer, that comes from https://github.com/blackducksoftware/integration-gradle-inspector. Filtering (including/excluding projects and configurations) is performed by this gradle/groovy code on the output of the `gradlew dependencies` command.

<a name="gradleparsedetector"></a>
# Gradle Parse Detector

TBD