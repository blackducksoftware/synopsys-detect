# Current Release notes

## Version 9.0.0

### New features

* Support for npm is now extended to npm 9.8.1.
* Support for npm workspaces.
* Support for Gradle is now extended to Gradle 8.2.
* Support for GoLang is now extended to Go 1.20.4.
* Support for Nuget package reference properties from Directory.Build.props and Project.csproj.nuget.g.props files.

### Changed features

* The `detect.diagnostic.extended` property and the -de command line option, that were deprecated in [solution_name] 8.x, have been removed. Use `detect.diagnostic`, and the command line option -d, instead.
* The Ephemeral Scan Mode, that was deprecated in [solution_name] 8.x, has been removed in favor of Stateless Scan Mode. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details.
* npm 6, which was deprecated in [solution_name] 8.x, is no longer supported.
* [solution_name] 7.x has entered end of support. See the [Product Maintenance, Support, and Service Schedule page](https://sig-product-docs.synopsys.com/bundle/blackduck-compatibility/page/topics/Support-and-Service-Schedule.html) for further details.
* The detectors\[N\].statusReason field of the status.json file will now contain the exit code of the detector subprocess command in cases when the code is non-zero.
  In the case of subprocess exit code 137, the detectors\[N\].statusCode and detectors\[N\].statusReason fields will be populated with a new status indicating a likely out-of-memory issue.
* In addition to node_modules, bin, build, .git, .gradle, out, packages, target, the Gradle wrapper directory `gradle` will be excluded from signature scan by default. Use
  [detect.excluded.directories.defaults.disabled](properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced) to disable these defaults.
* Removed reliance on [solution_name] libraries for init-detect.gradle script to prevent them from being included in the Gradle dependency verification of target projects.

### Resolved issues

* (IDETECT-3821) Detect will now capture and record failures of the Signature Scanner due to command lengths exceeding Windows limits. This can happen with certain folder structures when using the `detect.excluded.directories` property.
* (IDETECT-3820) Introduced an enhanced approach to NuGet Inspector for handling different formats of the `project.json` file, ensuring compatibility with both old and new structures.
* (IDETECT-4027) Resolved a problem with the npm CLI detector for npm versions 7 and later, which was causing only direct dependencies to be reported.
* (IDETECT-3997) Resolved npm package JSON parse detector issue of classifying components as RubyGems instead of npmjs.
* (IDETECT-4023) Resolved the issue of Scan failure if Project level "Retain Unmatched File Data" not set for "System Default".

### Dependency updates

* Upgraded Project Inspector to version 2021.9.10.
* Upgraded Nuget Inspector to version 1.1.0.
* Fixed EsotericSoftware YAMlBeans library version to resolve critical severity [CVE-2023-24621](https://nvd.nist.gov/vuln/detail/CVE-2023-24621)