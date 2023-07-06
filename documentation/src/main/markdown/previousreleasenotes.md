<!-- Check the support matrix to determine supported, non-current major version releases -->

# Release notes for supported versions

## Version 8.11.0

### Resolved issues

* (IDETECT-3921) [solution_name] will now validate directory permissions prior to downloading the [solution_name] JAR file.
=======
### New features

* For Stateless and Rapid scans, the scanId and Detector tool being run, are now stored in the codeLocations section of the status.json file containing the run summary.
* Stateless Signature and Package Manager scans now support the <code>--detect.blackduck.rapid.compare.mode</code> flag. Values are ALL, BOM_COMPARE, or BOM_COMPARE_STRICT. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details. 

## Version 8.10.0

### Changed features

* Leading and trailing spaces specified within quotes for `detect.project.name` or `detect.project.version.name` properties will now be trimmed.

### Resolved issues

* (IDETECT-3657) Resolved an issue where Intelligent Scans would fail if a project or version name included non-ASCII characters. 
* (IDETECT-3776) Resolved an issue with not detecting certain components in `go.mod` files as transitive dependencies when marked with `// indirect`, by improving identification of direct and indirect dependencies.
* (IDETECT-3817) Improved handling of large inspection results to prevent OutOfMemory exceptions and optimize memory usage.
* (IDETECT-3888) Improved the runtime performance of PIP Inspector for aws-cdk dependency cases by passing the package history list by reference instead of value.
* (IDETECT-3867) Resolved a lack of support for properties set in SPRING_APPLICATION_JSON environment variable for configuring [solution_name] when the Self Update feature is utilized.

### Dependency updates

* Upgraded Spring Boot to version 2.7.12 to resolve high severity [CVE-2023-20883](https://nvd.nist.gov/vuln/detail/CVE-2023-20883)
* Upgraded SnakeYAML to version 2.0 for [solution_name] air gap package to resolve critical severity [CVE-2022-1471](https://nvd.nist.gov/vuln/detail/CVE-2022-1471)
* Upgraded Jackson Databind to version 2.15.0 for [solution_name] air gap package to resolve high severity [CVE-2022-42003](https://nvd.nist.gov/vuln/detail/CVE-2022-42003) and [CVE-2022-42004](https://nvd.nist.gov/vuln/detail/CVE-2022-42004)
* Upgraded Project Inspector to version 2021.9.9


## Version 8.9.0

### New features

* [solution_name] Self Update feature will allow customers who choose to enable Centralized [solution_name] Version Management in [blackduck_product_name] to automate the update of [solution_name] across their pipelines. The Self Update feature will call the '/api/tools/detect' API to check for the existence of a mapped [solution_name] version in [blackduck_product_name]. If a version has been mapped, the API will redirect the request to download the specified version and the current execution of [solution_name] will invoke it to execute the requested scan. If no mapping exists, the current version of [solution_name] matches the mapped version in [blackduck_product_name], or if there is any issue during the execution of the Self Update feature, then [solution_name] will continue with the currently deployed version to execute the scan.
    * Centralized [solution_name] Version Management feature support in [blackduck_product_name] is available from [blackduck_product_name] version 2023.4.0 onwards.
    * See [Version Management](downloadingandinstalling/selfupdatingdetect.md) for more details.

### Changed features

* Release notes are now broken into sections covering the current, supported, and unsupported [solution_name] releases.
* npm 6 has reached end of life and is being deprecated. Support for npm 6 will be removed in [solution_name] 9.

### Resolved issues

* (IDETECT-3613) Resolved an issue where running a scan with `detect.maven.build.command=-Dverbose` caused a KB mismatch issue for omitted transitive dependencies.

### Dependency updates

* Upgraded SnakeYAML to version 2.0 to resolve critical severity [CVE-2022-1471]( https://nvd.nist.gov/vuln/detail/CVE-2022-1471)
* Upgraded Jackson Dataformat YAML to version 2.15.0 to resolve critical severity [CVE-2022-1471]( https://nvd.nist.gov/vuln/detail/CVE-2022-1471)
* Upgraded Spring Boot to version 2.7.11 to resolve high severity [CVE-2023-20873](https://nvd.nist.gov/vuln/detail/CVE-2023-20873)

## Version 8.8.0

### New features

* New Binary Stateless and Container Stateless Scans have been added to [solution_name]. These scans require the new detect.scaaas.scan.path property to be set to either a binary file or a compressed Docker image. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details.
<note type="attention">A Black Duck Binary Analysis (BDBA) license is required to execute these scan types.</note>

### Changed features

* Evicted dependencies in Simple Build Tool(SBT) projects will no longer be included in the Bill of Materials(BoM) generated during the scan.
* Introduced an optional flag to allow a space-separated list of global options to pass to all invocations of Project Inspector. Specify the <code>--detect.project.inspector.global.arguments</code> flag in the command, followed by other global flags if needed for pass through to Project Inspector. <br />
See [project-inspector properties for further details](properties/configuration/project-inspector.md).
* The maximum polling interval threshold is now dynamic when [solution_name] polls Black Duck for results. This dynamic threshold is dependent upon, and optimized for, the specific scan size. (The maximum polling threshold was formerly a fixed 60-second value.)

### Resolved issues

* (IDETECT-3111) When scanning SBT projects, "Evicted" dependencies are excluded from the resulting BOM.
* (IDETECT-3685) Gracefully handled use case when a Podfile.lock file has no PODS or dependencies in the generated dependency graph.
* (IDETECT-3738) Repositioned the global flags for inclusion before sub-commands for Project Inspector invocation.

## Version 8.7.0

### New features

* The accuracy of dependency determination, HIGH or LOW, of any detectors run during a scan will now be recorded in the status.json file.
* STATELESS/RAPID scans, when run against [blackduck_product_name] 2023.1.2 or later, will provide upgrade guidance for mitigation of vulnerabilities in transitive dependencies.

### Changed features

* Addition of command line help option, -hyaml, to generate a template configuration file.
* [solution_name]'s generated air gap zip is uploaded to Artifactory under the name "synopsys-detect-<version>-air-gap-no-docker.zip". Older naming patterns for this file are no longer supported.
* Failures in detectors will now be reported in the console output using the ERROR logging level. The ERROR logging is also used if there are errors in the overall status.

### Resolved issues

* (IDETECT-3661) [solution_name] will fail and echo the error received from [blackduck_product_name], if a problem occurs during the initiation of a Stateless Signature Scan.
* (IDETECT-3623) [solution_name] will now fail with exit code 3, FAILURE_POLICY_VIOLATION, if [blackduck_product_name] reports any violated policies during scans.
* (IDETECT-3630) Notices and risk report PDFs now appropriately contain the supplied project and version name when characters from non-English alphabets are used.
* (IDETECT-3654) As of version 8.0.0 of [solution_name], Cargo project dependency graphs stopped being post-processed. Previously, attempts to define parent relationships for dependencies when the Cargo.lock file is a flat list resulted in marking any dependencies with a parent relationship as Transitive. This meant a dependency, which if Direct, may appear as Transitive in [blackduck_product_name] if it is also a dependency of another component. BOMs created with 8.0.0 or later, no longer assume any relationships and all dependencies are DIRECT.

## Version 8.6.0

### Changed features

* Package Manager and Signature Scans will now query [blackduck_product_name] directly when using the detect.wait.for.results property. This expedites scanning by allowing [solution_name] to determine if results are ready, rather than waiting for a notification from [blackduck_product_name].
Note: this feature requires [blackduck_product_name] 2023.1.1 or later.

### Resolved issues

* (IDETECT-3627) When waiting for results, Signature Scans will now wait for all scans that the Signature Scan could invoke, such as Snippet and String Search scans. Previously, only the Signature Scan itself was checked for completion.
Note: this improvement requires [blackduck_product_name] 2023.1.2 or later. 

### Dependency updates

* Upgraded Apache Commons Text to version 1.10.0.
* Upgraded Docker Inspector to version 10.0.1.

## Version 8.5.0

### New features

* Added property blackduck.offline.mode.force.bdio which when set to true will force [solution_name] used in offline mode to create a BDIO even if no code locations were identified.

### Changed features

* The .yarn directory will now be ignored by default when determining which detectors are applicable to a project.
* An exit code of 2, representing FAILURE_TIMEOUT, will be returned when STATELESS scans do not report status in a timely fashion. The timeout can be controlled using the detect.timeout property.

## Version 8.4.0

### Changed features

* The flag value EPHEMERAL has been deprecated in favor of the value STATELESS.  See the [Stateless Scans page](runningdetect/statelessscan.md) for further details.

### Resolved issues

* (IDETECT-3384) Changed Warning message "No dependency found" in Lerna projects to Debug level.

## Version 8.3.0

### New features

* Added support for Reduced Persistence Signature Scanning. This feature allows users to specify if unmatched files should be persisted or discarded. Not storing data for unmatched files decreases scan time and database size. Note: this feature requires Black Duck 2022.10.0 or later.

### Resolved issues

* (IDETECT-3285) go.mod file "// indirects" matching as Direct Dependencies.  Additional information for the go project is obtained in order to definitively establish direct module dependencies and then establish which module dependencies are transitive.

* (IDETECT-3228) Resolved an issue that caused certain Maven dependency tree formats to not be parsed.

## Version 8.2.0

### New features

* Ephemeral Scan, or Ephemeral Scan Mode, is a new way of running [solution_name] with [blackduck_product_name]. This mode is designed to be as fast as possible and does not persist any data on [blackduck_product_name]. See the [Ephemeral Scans page](runningdetect/statelessscan.md) for further details.
* The output for Rapid and the new Ephemeral Scan Modes will now include upgrade guidance for security errors and warnings.

## Version 8.1.1

### Resolved issues

* (IDETECT-3509) Corrected the version of the NuGet Inspector built into the air gap zip files (from 1.0.1 to 1.0.2).

## Version 8.1.0

### New features

* Added support for Bazel project dependencies specified via a github released artifact location (URL) in an *http_archive* workspace rule.
* Added property detect.project.inspector.path to enable pointing [solution_name] to a local Project Inspector zip file.
* Added property detect.status.json.output.path to place a copy of the status.json file in a specified directory.

### Changed features

* Enhancements to error reporting to ensure that any exception will have the root cause reported in the error message for certain exception types.
* Overall Detect exit status is now being reported along with individual detector status/issues in the Status.json file.
* The __MACOSX directory will now be ignored by default when determining which detectors are applicable to a project.

### Resolved issues

* (IDETECT-3419) Resolved an issue where the NuGet inspector cannot be found when a solution file cannot be found but multiple C# projects are found by Detect.
* (IDETECT-3306) Resolved an issue where a NullPointerException would occur when project inspector discovered no modules for a project.
* (IDETECT-3307) Warn when project inspector cannot be downloaded, installed, or found.
* (IDETECT-3187) Report Black Duck provided error message (from response body) whenever a Black Duck api call returns an error code
* (IDETECT-3311) Include Detect's "Overall Status" in the status.json / diagnostic zip
* (IDETECT-3449) Resolved an issue that caused overridden violations to be reported as active violations when the BOM contained additional active violations.
* (IDETECT-3476) Resolved an issue that caused an "Input request parsing error" on IaC scans on certain projects when running on Windows.

## Version 8.0.0

### New features

* [solution_name] will now retry (until timeout; see property `detect.timeout`) BDIO2 uploads that fail with a non-fatal exit code.
* Added Detector cascade. Refer to [Detector search and accuracy](runningdetect/detectorcascade.md) for more information.

### Changed features

* The default value of `detect.project.clone.categories` now includes DEEP_LICENSE (added to Black Duck in 2022.2.0), raising the minimum version of Black Duck for [solution_name] 8.0.0 to 2022.2.0.
* The [codelocation naming scheme](naming/projectversionscannaming.md#code-location-scan-naming) has changed. To prevent old codelocations from contributing stale results to re-scanned projects, set property `detect.project.codelocation.unmap` to true for the first run of [solution_name] 8. This will unmap the old codelocations.
* The default value of `detect.force.success.on.skip` has changed to false, so by default [solution_name] will exit with return code FAILURE_MINIMUM_INTERVAL_NOT_MET (13) when a scan is skipped because the Black Duck minimum scan interval has not been met.
* By default, all detectors now include in their dependency graph all discovered
dependencies, packages, and configurations, because the default for
properties `detect.*.[dependency|package|configuration].types.excluded` is NONE. 
This is a change in the default behavior for the following detector types: 
GO_MOD, GRADLE, LERNA, RUBYGEMS.
* Dropped NONE as a supported value for the following properties: `detect.included.detector.types`, `detect.tools`.
* Dropped ALL as a supported value for the following properties: `detect.excluded.detector.types`, `detect.tools.excluded`.
* Removed support for parsing SBT report files.
* Cargo project dependency graphs are no longer post-processed to reduce direct dependencies in the BOM.
* Removed the ability to upload BDIO2 documents to legacy endpoints via the `blackduck.legacy.upload.enabled` property.
* Removed the ability to choose the type of BDIO aggregation strategy via the now removed `detect.bom.aggregate.remediation.mode` property.  All BDIO will be aggregated in a manner similar to [solution_name] 7's SUBPROJECT remediation mode.
* [solution_name] now only produces a single Scan in Black Duck for Detectors, named (by default) "\<projectName\>/\<projectVersion\> Black Duck I/O Export". 
* detect8.sh has improvements (relative to detect7.sh and detect.sh) related to argument handling that simplify its argument quoting/escaping requirements.
* [solution_name] requires and runs [docker_inspector_name] version 10.
* Incorporated [docker_inspector_name] documentation into [solution_name] documentation.
* The search for files for binary scanning (when property `detect.binary.scan.file.name.patterns` is set) now excludes directories specified by property `detect.excluded.directories`.
* The status.json field `detectors[n].descriptiveName` (which was simply a hyphen-separated concatenation of the `detectorType` and `detectorName` fields) has been removed.
* There is no longer a distinction between extended and non-extended diagnostic zip files. All diagnostic zip files now include all relevant files.
* The following properties (that were deprecated in [solution_name] 7.x) have been removed: `blackduck.legacy.upload.enabled`, `detect.bazel.dependency.type`,
`detect.bdio2.enabled`, `detect.bom.aggregate.name`, `detect.bom.aggregate.remediation.mode`, `detect.conan.include.build.dependencies`, `detect.detector.buildless`,
`detect.docker.path.required`, `detect.dotnet.path`, `detect.go.mod.enable.verification`, `detect.gradle.include.unresolved.configurations`, `detect.gradle.inspector.version`,
`detect.lerna.include.private`, `detect.maven.buildless.legacy.mode`, `detect.maven.include.plugins`, `detect.npm.include.dev.dependencies`, `detect.npm.include.peer.dependencies`,
`detect.nuget.inspector.version`, `detect.packagist.include.dev.dependencies`, `detect.pear.only.required.deps`, `detect.pnpm.dependency.types`,
`detect.pub.deps.exclude.dev`, `detect.ruby.include.dev.dependencies`, `detect.ruby.include.runtime.dependencies`, `detect.sbt.excluded.configurations`,
`detect.sbt.included.configurations`, `detect.sbt.report.search.depth`, `detect.yarn.prod.only`.

### Resolved issues

* (IDETECT-3375) Resolved an issue where [solution_name] would unnecessarily upload empty BDIO entry file when initiating an IaC scan.
* (IDETECT-3224) Resolved an issue where Cargo projects with Cyclical dependencies could cause a failure of [solution_name].
* (IDETECT-3246) Resolved an issue where [solution_name] would fail when scanning flutter projects after a new version of flutter was released.
* (IDETECT-3275) Resolved an issue that caused impact analysis to fail with an "Unsupported class file major version" error when an analyzed .class file contained invalid version bytes (byte 7 and 8).
* (IDETECT-3180) Resolved an issue that caused the Binary Search tool to throw an exception when the patterns provided via property detect.binary.scan.file.name.patterns matched one or more directories.
* (IDETECT-3352) Resolved an issue that caused the Gradle Project Inspector detector to fail when the value of detect.output.path was a relative path.
* (IDETECT-3371) Resolved an issue that could cause some transitive dependencies to be omitted from aggregated BDIO in cases where the transitive dependencies provided by the package manager for a component differed across subprojects.

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

## Version 7.11.1

### Changed features

* Updated [solution_name] to package Air Gap with the latest Nuget Inspectors: IntegrationNugetInspector:3.1.1, BlackduckNugetInspector:1.1.1, NugetDotnet3Inspector:1.1.1,
  NugetDotnet5Inspector:1.1.1.

## Version 7.11.0

### New features

* Added a feature that allows users to set a license for a project version using the
  property [detect.project.version.license](properties/configuration/project.md#project-version-license).
* Added support for identifying dependency relationships between Linux package manager components in images.

### Changed features

* The Go Mod Cli Detector no longer uses the "-u" flag when running **go list -m all**. This results in significantly faster scan times against Go Mod projects.
* Deprecated the `detect.pnpm.dependency.types` property in favor of `detect.pnpm.dependency.types.excluded` for property consistency.

### Resolved issues

* (IDETECT-2925) Resolved an issue that could cause the Bitbake detector to incorrectly identify the layer of a dependency recipe.
* (IDETECT-3080) Fixed an issue where [solution_name] would not include multiple versions of the same package in Cargo projects.
* (IDETECT-3012) Resolved an issue that caused [solution_name] to incorrectly use BLACKDUCK_USERNAME and BLACKDUCK_PASSWORD.

## Version 7.10.0

### New features

* Added support for the Apache Ivy package manager.
* Build dependencies can now be excluded from BitBake results.

### Changed features

* [solution_name] now classifies empty code location warning messages as the DEBUG logging level instead of the previous classification as the WARN logging level.
* BitBake detector: Added support for BitBake 1.52 (Yocto 3.4).
* BitBake detector: Added support for BitBake projects with build directories that reside outside the project directory.
* Deprecated many properties relating to filtering dependency types from the BOM. These property replacements will reduce the number of properties, apply consistency to detector
  properties, add filtering abilities, and overall simplify Detect configuration.
    * Deprecated the following properties:
        * detect.conan.include.build.dependencies
        * detect.pub.deps.exclude.dev
        * detect.go.mod.enable.verification
        * detect.gradle.include.unresolved.configurations
        * detect.lerna.include.private
        * detect.npm.include.dev.dependencies
        * detect.npm.include.peer.dependencies
        * detect.packagist.include.dev.dependencies
        * detect.pear.only.required.deps
        * detect.ruby.include.runtime.dependencies
        * detect.ruby.include.dev.dependencies
        * detect.yarn.prod.only
    * Added the following replacement properties:
        * detect.conan.dependency.types.excluded
        * detect.pub.dependency.types.excluded
        * detect.go.mod.dependency.types.excluded
        * detect.gradle.configuration.types.excluded
        * detect.lerna.package.types.excluded
        * detect.npm.dependency.types.excluded
        * detect.packagist.dependency.types.excluded
        * detect.pear.dependency.types.excluded
        * detect.ruby.dependency.types.excluded
        * detect.yarn.dependency.types.excluded

### Resolved issues

* (IDETECT-2949) Fixed an issue where [solution_name] failed to properly parse Go module version names containing '-' characters.
* (IDETECT-2959) Fixed an issue where [solution_name] would not fail when running `go mod why` fails.
* (IDETECT-2971) Fixed an issue where [solution_name] would not produce unique code location paths for Pnpm projects.
* (IDETECT-2939) Fixed an issue where NPM projects that had no declared dependencies would not exclude peer or dev dependencies.
* (IDETECT-3038) Fixed an issue where [solution_name] would fail to parse file dependency declarations for pnpm projects in their pnpm-lock.yaml files.
* (IDETECT-3000) Fixed an issue where [solution_name] would error out when a user's source directory and output directory did not share a common root.

## Version 7.9.0

### New features

* Added support for the Xcode Swift Package Manager for Xcode projects using the built-in [Swift Packages](https://developer.apple.com/documentation/swift_packages) feature.
* Added detect.bdio.file.name to specify the name of the output BDIO file.
* Added system architecture DEBUG level logs to assist with support.

### Changed features

* The version of each package manager tool executed by CLI detectors is now logged at DEBUG level.

### Resolved issues

* (IDETECT-2499) Fixed an issue in the Gradle Inspector that caused it to exclude all identically named subprojects except one.
* (IDETECT-2953) Fixed the project and project version links in risk report.
* (IDETECT-2989) Fixed an issue with Go Mod projects where [solution_name] included unused transitive dependencies, despite detect.go.mod.enable.verification being set to 'true'.
* (IDETECT-2935) Verified that [solution_name] is compatible with Gradle version 7.X.

## Version 7.8.0

### New features

* Added support for the pnpm package manager.
* Added property detect.project.group.name for setting the Project Group.
* [solution_name] now falls back to using a previously downloaded Docker Inspector, Project Inspector, and/or NuGet Inspector when https://sig-repo.synopsys.com is unreachable.

## Version 7.7.0

### New features

* Added support for uploading rapid scan config file when a file named '.bd-rapid-scan.yaml' is present in the source directory.
* Added the property detect.project.inspector.arguments for providing additional arguments to the project inspector across all invocations.

### Resolved issues

* (IDETECT-2808, IDETECT-2863) Resolved an issue where [solution_name] would incorrectly resolve relative paths when processing signature scan targets.
* (IDETECT-2859) Resolved an issue where [solution_name] was using an outdated cookie spec when making a request, resulting in a warning message.

## Version 7.6.0

### New features

* Added the property [detect.follow.symbolic.links](properties/configuration/general.md#follow-symbolic-links) which can be used to enable [solution_name] to follow symbolic links
  when searching directories for detectors, when creating exclusions for signature scan, and when creating binary scan targets.
* Added support for Open Container Initiative (OCI) images provided to [solution_name] using the *detect.docker.tar* property.
* Added the property [detect.gradle.include.unresolved.configurations](properties/detectors/gradle.md#gradle-include-unresolved-configurations-advanced) for toggling the inclusion
  of [unresolved Gradle configurations](https://docs.gradle.org/7.2/userguide/declaring_dependencies.html#sec:resolvable-consumable-configs).
* Added Project Inspector support for MAVEN and GRADLE when resolving buildless dependencies.
* Added support for NUGET buildless using the Project Inspector.

### Changed features

* The detect.project.clone.categories property now supports ALL and NONE as options.
* The the default value for property detect.project.clone.categories has changed to ALL.
* Deprecated detect.bom.aggregate.name, detect.bom.aggregate.remediation.mode, and blackduck.legacy.upload.enabled. In version 8, [solution_name] will only operate in SUBPROJECT
  aggregation mode to report the dependency graph with greater accuracy.
* Maven defaults to the legacy buildless parser, Project Inspector must be enabled with detect.maven.buildless.legacy.mode. In version 8, it will default to Project Inspector.
* Deprecated detect.maven.include.plugins as Project Inspector does not support plugins. In version 8, we will only support the Project Inspector Maven implementation which will
  have its own configuration mechanism.
* The Air Gap Zip generation options no longer support individual package managers. Instead, either a FULL air gap can be created, or a NO_DOCKER air gap can be created. This is to
  help support project inspector which spans multiple package managers.

### Resolved issues

* (IDETECT-2834) Resolved an issue where GoMod components missing a version were not being properly filtered causing a NullPointerException.
* (IDETECT-2829) Resolved an issue that caused [solution_name] to use the wrong scan cli when in offline mode and ignore a specified local scan cli.
* (IDETECT-2820) Resolved an issue where pypi components in conda projects were not being matched.
* (IDETECT-2773) Resolved an issue where [solution_name] was not replacing module paths as specified in go mod replace statements.

## Version 7.5.0

### New features

* Added support for the Dart package manager.

### Changed features

* The following directories are no longer excluded from Signature Scan by default: bin, build, out, packages, target. .synopsys directories are now excluded from both Detector
  search and Signature Scan.
* The Docker Inspector can now be included (using the detect.tools property) when using
  the [rapid scan mode](properties/configuration/blackduck-server.md#detect-scan-mode-advanced).
* Instead of "lite" Docker images that automatically disable all detectors, [solution_name] now supports "buildless" Docker images that automatically disable detectors that depend
  on the presence of build tools but leave buildless detectors enabled.

### Resolved issues

* (IDETECT-2830) Resolved an issue that caused the Gradle detector to fail when run in air gap mode.
* (IDETECT-2816) Resolved an issue that caused a "Duplicate key" error when running binary scan on multiple files with the same name.

## Version 7.4.0

### New features

* Added SUBPROJECT remediation mode, invoked using property detect.bom.aggregate.remediation.mode. Use only with Black Duck 2021.8.0 or later.

## Version 7.3.0

### New features

* Added support for the Carthage package manager.

### Changed features

* The Poetry detector is no longer categorized as a PIP detector and is now categorized under detector type POETRY.
* Simplified the property deprecation lifecycle to the following: Use of deprecated properties will result in logged warnings until the next major version release, at which time
  those properties will be removed from Detect (and ignored if used). Properties that were deprecated in Detect 6.x have been removed in this release. Properties deprecated in
  Detect 7.x will be removed in Detect 8.0.0.

## Version 7.2.0

### Changed features

* Improved the readability of Rapid mode results.

### Resolved issues

* (IDETECT-2532) Resolved an issue that could cause multiple versions of Go-Mod dependencies to appear in the BOM.
* (IDETECT-2668) Resolved an issue that caused Go-Mod dependencies with a replacement version to be omitted.
* (IDETECT-2722) Resolved an issue that caused the version to be omitted from Go-Vendr dependencies when the vendor.conf separated dependency name and version with multiple space
  characters.
* (IDETECT-2672) Resolved an issue that could cause the Black Duck access token to appear in the log.
* (IDETECT-2739) Resolved an issue that caused the default to be used when the provided Risk Report path did not exist.

## Version 7.1.0

### New features

* Added ability to specify custom fonts to be used during risk report generation. See [here](results/reports.md#risk-report-generation) for more details.
* There now exist Docker images that can be used to run [solution_name] from within a container.
  See [Running Synopsys Detect from within a Docker container](runningdetect/runincontainer.md#running-synopsys-detect-from-within-a-docker-container) for more details.
* Added detect.go.mod.enable.verification for disabling the `go mod why` check that [solution_name] uses to filter out unused dependencies.
* Added support for dotnet 5 when running the NuGet inspector.
* Added a new property [detect.npm.include.peer.dependencies](properties/detectors/npm.md#include-npm-peer-dependencies) which allows the users to filter out NPM peer dependencies
  from their BOM.

### Changed features

* The following clone categories were added to the default value for property detect.project.clone.categories: LICENSE_TERM_FULFILLMENT, CUSTOM_FIELD_DATA
* The "Git Cli" detector has been renamed to the "Git" detector.
* Whenever [solution_name] runs the following tools, it now logs (at level DEBUG) the tool's version: git, gradle, maven, conan, pip, and python.

### Resolved issues

* (IDETECT-2541) Resolved an issue that caused the CLANG detector to fail when run in non-English locales on Ubuntu and Debian systems.
* (IDETECT-2505) Resolved an issue that caused go mod components with +incompatible version suffixes to not be matched on Black Duck.
* (IDETECT-2629) Resolved an issue that caused go mod projects without source having an empty BOM with the introduction of the detect.go.mod.enable.verification property.
* (IDETECT-2659) Resolved an issue that caused [solution_name] to falsely report a missing detector when that detector matched only at a depths > 0 and was included in the value of
  property detect.required.detector.types.
* (IDETECT-2696) Resolved an issue that could cause [solution_name] to fail with "IllegalStateException: Duplicate key {codelocation name}" when creating >100 codelocations in one
  run.
* (IDETECT-2659) Resolved an issue that caused Detect to falsely report "One or more required detector types were not found" when the required detector ran based on files found in
  a subdirectory.
* (IDETECT-2541) Resolved an issue that caused the CLANG detector to fail with "Unable to execute any supported package manager" when run with a non-English locale on an alpine
  system.

## Version 7.0.0

### New features

* Added scripts detect7.sh and detect7.ps1 for invoking [solution_name] 7.x.x. detect.sh and detect.ps1 will (by default) continue to invoke the latest [solution_name] 6 version.
* Added support for Yarn workspaces.
* Added support for the dependency graph SBT plugin. Resolution cache generation is no longer a requirement of the SBT detector.
* Added the properties [detect.excluded.directories](properties/configuration/paths.md#detect-excluded-directories-advanced),
  [detect.excluded.directories.defaults.disabled](properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced),
  and [detect.excluded.directories.search.depth](properties/configuration/signature-scanner.md#detect-excluded-directories-search-depth) to handle exclusions for detector search
  and signature scanning.
* Added ability to specify excluded directory paths
  using [glob patterns](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)).
* Added properties [detect.lerna.excluded.packages](properties/detectors/lerna.md#lerna-packages-excluded-advanced)
  and [detect.lerna.included.packages](properties/detectors/lerna.md#lerna-packages-included-advanced) to exclude and include specific Lerna packages.
* Added critical security risks to the Black Duck Risk Report pdf.
* Added detect.target.type to enhance the docker user experience. When set to IMAGE, some tools are automatically disabled and detect optimizes for an image-based scan.
* Added binary scanning of the container filesystem to the default Docker image scanning workflow. If you are scanning Docker images and your Black Duck server does not have the
  binary scanning feature enabled; use --detect.tools.exluded=BINARY_SCAN to disable the binary scan step.

### Changed features

* The following directories will be excluded from signature scan by default, in addition to node_modules: bin, build, .git, .gradle, out, packages, target. Use
  [detect.excluded.directories.defaults](properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced) to disable these defaults.
* Detect no longer supports the exclusion of individual files during detector search, only directories.
* Gradle detector no longer uses the gradle inspector. Only the init script is required.
* The default BDIO format for communicating dependency graphs to Black Duck has been changed from BDIO1 to BDIO2.
* Risk report generation will download fonts from Artifactory or use the font files in the fonts directory in the air gap zip of detect.

### Resolved issues

* (IDETECT-2462) Resolved an issue where projects were being inaccurately diagnosed as Poetry projects due to the presence of a pyproject.toml file.
* (IDETECT-2527) Resolved an issue in the Go Mod detector to extract and process data even if 'go mod why' command fails to run.
* (IDETECT-2434) Resolved an issue in the CLANG detector on Ubuntu and Debian systems that caused it to omit a package when that package had been installed on the system from
  multiple architectures.
* (IDETECT-2362) The CLANG detector now uses the KB preferred alias namespace feature for improved match accuracy.
* (IDETECT-2413) Resolved an issue to upgrade internal dependencies to support JDK 15.
* (IDETECT-2409) Resolved an issue to allow Gradle detector to support Gradle 6.8.
* (IDETECT-2099) Improved error reporting for exceptions that occur during a Detect run. For each exception, a Detect "issue" is written to the log and to the status.json file.
* (IDETECT-2516) Improved error reporting for the case where environment variable BDS_JAVA_HOME is set incorrectly.
