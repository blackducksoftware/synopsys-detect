# Current Release notes

## Version 9.5.0

### New features

* [company_name] [solution_name] now includes the maven embedded or shaded dependencies as part of the Bill of Materials (BOM) via the property --detect.maven.include.shaded.dependencies. See the [detect.maven.include.shaded.dependencies](properties/detectors/maven.md#maven-include-shaded-dependencies) property for more information.
* [company_name] [solution_name] buildless mode now supports the exclusion of maven dependencies having "\<exclude\>" tags in the pom file and honours effects of dependency scope during Maven Dependency Resolution.

### Dependency updates

* Upgraded Project Inspector to version 2024.2.0. Please refer to [maven](packagemgrs/maven.md), [gradle](packagemgrs/gradle.md) and [nuget](packagemgrs/nuget.md) for more information on the changes.
  <note type="information"> From [company_name][solution_name] 9.5.0, we will only support and be compatible with the mentioned version of Project Inspector.</note>