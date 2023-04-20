<!-- Check the support matrix to determine supported, non-current major version releases -->

# Release notes for supported versions

## Version 7.14.0

### New features
* Added support for Swift projects built with Swift 5.6 or later.

* Added support for running IaC scans via [solution_name]. See [IaC Scan](runningdetect/iacscan.md) for more details. Note: IaC capabilities require Black Duck 2022.7.0 or later.

## Version 7.13.2

* (IDETECT-3291) Resolved an issue where the NuGet Inspector would only be found for the first applicable detector.
* (IDETECT-3289) Resolved an issue where the NuGet Inspector could not handle Implicit Dependencies in a Package Reference. 

## Version 7.13.1

* (IDETECT-3286) Resolved an issue that caused the 7.13.0 .jar to be unsigned. The 7.13.1 .jar is signed.

## Version 7.13.0

### New features
* Added support for a buildless Pipenv detector that parses the Pipfile.lock file (see the [python support page](packagemgrs/python.md) for more details).
* [solution_name] now includes pass-through properties when logging configuration at the beginning of a run.
* Added support for Xcode Workspaces (see the [swift support page](packagemgrs/swift.md) for more details).

### Changed features
* Nuget Inspector is now shipped as a self-contained executable and has no runtime requirements. 
* Deprecated property detect.detector.buildless, to be replaced with detect.accuracy.required. See [property description](properties/configuration/detector.md#detector-accuracy-requirements-advanced) for more details.

### Resolved issues
* (IDETECT-3136) Resolved an issue where NPM's `package-lock.json` was prioritized over `npm-shrinkwrap.json`.
* (IDETECT-3184) Resolved an issue that prevented matches for Bazel maven_install components with complex (>3 parts) maven_coordinates values.
* (IDETECT-3207) Resolved an issue that prevented Bazel and Docker Tool issues from being reported in the issues section of the [solution_name] log and status file.

### Dependency update

* Upgraded to Spring Boot version 2.6.6 / Spring version 5.3.18.

## Version 7.12.1

### Changed Features

* When signature scanning is skipped due to the minimum scan interval on Black Duck not being met, [solution_name] by default will treat the run as a success and will not wait for the skipped scan(s). If the property detect.force.success.on.skip is set to false, [solution_name] will instead return exit code 13 when one or more signature scans were skipped.

## Version 7.12.0

### New features

* Verified support for Java 16 and 17.
* Added new properties detect.gradle.excluded.project.paths and detect.gradle.included.project.paths to allow filtering on paths which gradle guarantees to be unique.
* Added support for vendoring Go Mod dependencies using [detect.go.mod.dependency.types.excluded=VENDORED](properties/detectors/go.md#go-mod-dependency-types-excluded) to exclude *test* and *build system* dependencies from Go modules declaring a version prior to `Go 1.16`.
* Added a feature that allows users to configure [solution_name] to fail when policies of a certain name are violated.
  See [detect.policy.check.fail.on.names](properties/configuration/project.md#fail-on-policy-names-with-violations) for details. Note: this feature requires Black Duck 2022.2.0 or later.
* Added Rapid Compare Mode which enables returning only the differences in policy violations compared to a previous scan.

### Changed features

* Changed default value of detect.project.clone.categories from ALL to COMPONENT_DATA, CUSTOM_FIELD_DATA, LICENSE_TERM_FULFILLMENT, VERSION_SETTINGS, VULN_DATA.  This avoids the automatic setting of the clone category DEEP_LICENSE introduced in Black Duck 2022.2.0. Users of [solution_name] 7.12.0 that wish to pass DEEP_LICENSE or ALL as a value to detect.project.clone.categories must be using Black Duck 2022.2.0 or later.
* Added new property detect.bazel.workspace.rules to replace the now deprecated detect.bazel.dependency.type property.
* For Go Mod projects, successfully executing `go version` is now required. Unsuccessful attempts now result in a run failure.
* The property *detect.go.mod.dependency.types.excluded* now only accepts a single value rather than a list of values.

### Resolved issues

* (IDETECT-3016) Resolved an issue where proxies may block HEAD requests made by [solution_name] when attempting to download the Signature Scanner from Black Duck. Because the
  criteria that [solution_name] uses to download the Black Duck Signature Scanner is new, the next run will re-download the Signature Scanner.
* (IDETECT-3165) Resolved an issue that could cause the Bitbake detector to fail with error `Graph Node recipe ... does not correspond to any known layer`.
