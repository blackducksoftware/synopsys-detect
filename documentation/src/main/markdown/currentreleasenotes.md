# Current Release notes

## Version 9.4.0

### New features

* Nuget Inspector now supports the exclusion of user-specified dependency types from the Bill of Materials (BOM) via the [solution_name] property --detect.nuget.dependency.types.excluded. See the [detect.nuget.dependency.types.excluded](properties/detectors/nuget.md#nuget-dependency-types-excluded) property for more information.
* Support for BitBake is now extended to 2.6 (Yocto 4.3.2).

### Changed features

* 

### Resolved issues

* (IDETECT-4155) Improved input validation in Component Location Analysis.
* (IDETECT-4187) Removed references to 'murex' from test resources.

### Dependency updates

* Released and Upgraded Nuget Inspector to version 1.3.0.
* Released and Upgraded Detect Docker Inspector to version 10.1.1.
