# Current Release notes

## Version 9.4.0

### New features

* Nuget Inspector now supports the exclusion of user-specified dependency types from the Bill of Materials (BOM) via the [solution_name] property --detect.nuget.dependency.types.excluded. See the [detect.nuget.dependency.types.excluded](properties/detectors/nuget.md#nuget-dependency-types-excluded) property for more information.
* A new detector for Python packages has been added. The PIP Requirements File Parse is a buildless detector that acts as a LOW accuracy fallback for the PIP Native Inspector. This detector is triggered for PIP projects that contain one or more requirements.txt files if [solution_name] does not have access to a PIP executable in the environment where the scan is run.
	* See [PIP Requirements File Parse](packagemgrs/python.md).
* To improve Yarn detector performance a new parameter is now available. The `--detect.yarn.ignore.all.workspaces` parameter enables the Yarn detector to build the dependency graph without analysis of workspaces. The default setting for this parameter is false and must be set to true to be enabled.
	* See [Yarn support](packagemgrs/yarn.md).
* Support for BitBake is now extended to 2.6 (Yocto 4.3.2).
* Support for Yarn extended to include Yarn 3 and Yarn 4.

### Changed features

* Key-value pairs specified as part of the `detect.blackduck.signature.scanner.arguments` property will now replace the values specified elsewhere, rather than act as additions.

### Resolved issues

* (IDETECT-4155) Improved input validation in Component Location Analysis.
* (IDETECT-4187) Removed references to 'murex' from test resources.

### Dependency updates

* Released and Upgraded Nuget Inspector to version 1.3.0.
* Released and Upgraded Detect Docker Inspector to version 10.1.1.
