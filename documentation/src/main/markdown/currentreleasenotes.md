# Current Release notes

## Version 9.2.0

### New features

* Support for pnpm is now extended to 8.9.2.
* Nuget support extended to version 6.2 with Central Package Management now supported for projects and solutions.

### Changed features

* pnpm 6, and pnpm 7 using the default v5 pnpm-lock.yaml file, are being deprecated. Support will be removed in [solution_name] 10.

### Resolved Issues

(IDETECT-3515) Resolved an issue where the Nuget Inspector was not supporting "<Version>" tags for "<PackageReference>" on the second line and was not cascading to Project Inspector in case of failure.

## Version 9.1.0

### New features

* Container Scan. Providing component risk detail analysis for each layer of a container image, (including non-Linux, non-Docker images). Please see [Container Scan ](runningdetect/containerscanning.md) for details.
	<note type="restriction">Your [blackduck_product_name] server must have [blackduck_product_name] Secure Container (BDSC) licensed and enabled.</note>
* Support for Dart is now extended to Dart 3.1.2 and Flutter 3.13.4.
* Documentation for [CPAN Package Manager](packagemgrs/cpan.md) and [BitBucket Integration](integrations/bitbucket/bitbucketintegration.md) has been added.

### Changed features

* When [blackduck_product_name] version 2023.10.0 or later is busy and includes a retry-after value greater than 0 in the header, [solution_name] will now wait the number of seconds specified by [blackduck_product_name] before attempting to retry scan creation. 
	* [solution_name] 9.1.0 will not retry scan creation with versions of [blackduck_product_name] prior to 2023.10.0

### Resolved issues

* (IDETECT-3843) Additional information is now provided when [solution_name] fails to update and [solution_name] is internally hosted.
* (IDETECT-4056) Resolved an issue where no components were reported by CPAN detector.
  If the cpan command has not been previously configured and run on the system, [solution_name] instructs CPAN to accept default configurations.
* (IDETECT-4005) Resolved an issue where the location is not identified for a Maven component version when defined as a property.
* (IDETECT-4066) Resolved an issue of incorrect TAB width calculation in Component Locator.

### Dependency updates

* Upgraded [solution_name] Alpine Docker images (standard and buildless) to 3.18 to pull the latest curl version with no known vulnerabilities.
* Removed curl as a dependency from [solution_name] Ubuntu Docker image by using wget instead of curl.


## Version 9.0.0

### New features

* Support for npm is now extended to npm 9.8.1.
* Support for npm workspaces.
* Lerna projects leveraging npm now support npm up to version 9.8.1.
* Support for Gradle is now extended to Gradle 8.2.
* Support for GoLang is now extended to Go 1.20.4.
* Support for Nuget package reference properties from Directory.Build.props and Project.csproj.nuget.g.props files.

### Changed features

* The `detect.diagnostic.extended` property and the -de command line option, that were deprecated in [solution_name] 8.x, have been removed. Use `detect.diagnostic`, and the command line option -d, instead.
* The Ephemeral Scan Mode, that was deprecated in [solution_name] 8.x, has been removed in favor of Stateless Scan Mode. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details.
* npm 6, which was deprecated in [solution_name] 8.x, is no longer supported.
* The detectors\[N\].statusReason field of the status.json file will now contain the exit code of the detector subprocess command in cases when the code is non-zero.
  In the case of subprocess exit code 137, the detectors\[N\].statusCode and detectors\[N\].statusReason fields will be populated with a new status indicating a likely out-of-memory issue.
* In addition to node_modules, bin, build, .git, .gradle, out, packages, target, the Gradle wrapper directory `gradle` will be excluded from signature scan by default. Use
  [detect.excluded.directories.defaults.disabled](properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced) to disable these defaults.
* Removed reliance on [solution_name] libraries for init-detect.gradle script to prevent them from being included in the Gradle dependency verification of target projects.   
<note type="notice">[solution_name] 7.x has entered end of support. See the [Product Maintenance, Support, and Service Schedule page](https://sig-product-docs.synopsys.com/bundle/blackduck-compatibility/page/topics/Support-and-Service-Schedule.html) for further details.</note>

### Resolved issues

* (IDETECT-3821) Detect will now capture and record failures of the Signature Scanner due to command lengths exceeding Windows limits. This can happen with certain folder structures when using the `detect.excluded.directories` property.
* (IDETECT-3820) Introduced an enhanced approach to NuGet Inspector for handling different formats of the `project.json` file, ensuring compatibility with both old and new structures.
* (IDETECT-4027) Resolved a problem with the npm CLI detector for npm versions 7 and later, which was causing only direct dependencies to be reported.
* (IDETECT-3997) Resolved npm package JSON parse detector issue of classifying components as RubyGems instead of npmjs.
* (IDETECT-4023) Resolved the issue of Scan failure if Project level "Retain Unmatched File Data" not set for "System Default".

### Dependency updates

* Released and Upgraded Project Inspector to version 2021.9.10.
* Released and Upgraded Nuget Inspector to version 1.1.0.
* Fixed EsotericSoftware YAMLBeans library version to resolve critical severity [CVE-2023-24621](https://nvd.nist.gov/vuln/detail/CVE-2023-24621)
