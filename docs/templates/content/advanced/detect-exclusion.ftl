# Exclusions in Detect

## Directory Exclusions

Use [detect.excluded.directories](../../properties/configuration/paths/#detect-excluded-directories-advanced) to exclude directories from search when looking for detectors, and when finding paths to pass to the signature scanner as values for a '--exclude' flag.

### Exclude directories by name

This property accepts explicit directory names, as well as globbing-style wildcard patterns. See [here](../includeexcludewildcards/#property-wildcard-support) for more info.

### Exclude directories by path

This property accepts explicit paths relative to your source's root, or you may specify glob-style patterns.

${solution_name} uses FileSystem::getPatchMatcher and its glob syntax implementation to exclude path patterns. See [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)) for more info.

Related properties:

* [detect.exclude.default.directories](../../properties/configuration/paths/#detect-exclude-default-directories-advanced)
* [detect.excluded.directory.search.depth](../../properties/configuration/signature scanner/#detect-excluded-directory-search-depth)

## Package Manager Exclusions

If you wish to specify package manager-specific exclusions you may do so using the following properties:

* [detect.gradle.included.configurations](../../properties/detectors/gradle/#gradle-include-configurations-advanced)
* [detect.gradle.excluded.configurations](../../properties/detectors/gradle/#gradle-exclude-configurations-advanced)
* [detect.gradle.included.projects](../../properties/detectors/gradle/#gradle-include-projects-advanced)
* [detect.gradle.excluded.projects](../../properties/detectors/gradle/#gradle-exclude-projects-advanced)
* detect.lerna.included.packages
* detect.lerna.excluded.packages
* [detect.maven.included.scopes](../../properties/detectors/maven/#dependency-scope-included)
* [detect.maven.excluded.scopes](../../properties/detectors/maven/#dependency-scope-excluded)
* [detect.maven.included.modules](../../properties/detectors/maven/#maven-modules-included-advanced)
* [detect.maven.excluded.modules](../../properties/detectors/maven/#maven-modules-excluded-advanced)
* [detect.nuget.included.modules](../../properties/detectors/nuget/#nuget-projects-included-advanced)
* [detect.nuget.excluded.modules](../../properties/detectors/nuget/#nuget-projects-excluded-advanced)
* [detect.sbt.included.configurations](../../properties/detectors/sbt/#sbt-configurations-included-deprecated)
* [detect.sbt.excluded.configurations](../../properties/detectors/sbt/#sbt-configurations-excluded-deprecated)