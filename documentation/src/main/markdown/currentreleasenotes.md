# Current Release notes

## Version 9.5.0

### New features

* [company_name] [solution_name] now includes the maven embedded or shaded dependencies as part of the Bill of Materials (BOM) via the property --detect.maven.include.shaded.dependencies. See the [detect.maven.include.shaded.dependencies](properties/detectors/maven.md#maven-include-shaded-dependencies) property for more information.
* [company_name] [solution_name] Maven Project Inspector now supports the exclusion of maven dependencies having "\<exclude\>" tags in the pom file.
* [company_name] [solution_name] Maven Project Inspector and Gradle Project Inspector honours effects of dependency scopes during dependency resolution.

### Dependency updates

* Upgraded Project Inspector to version 2024.2.0. Please refer to [Maven](packagemgrs/maven.md), [Gradle](packagemgrs/gradle.md) and [Nuget](packagemgrs/nuget.md) documentation for more information on the changes.
  As of version 9.5.0 [company_name][solution_name] will only be compatible with, and support, Project Inspector 2024.2.0 or later.