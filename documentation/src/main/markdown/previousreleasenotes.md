<!-- Check the support matrix to determine supported, non-current major version releases -->

# Release notes for supported versions

## Version 8.11.0

### New features

* For Stateless and Rapid scans, the scanId and scan type being run are now stored in the codeLocations section of the status.json file. For a given scanId, the scan type can be DETECTOR, BINARY_SCAN, SIGNATURE_SCAN, or CONTAINER_SCAN.
* Stateless Signature and Package Manager scans now support the <code>--detect.blackduck.rapid.compare.mode</code> flag. Values are ALL, BOM_COMPARE, or BOM_COMPARE_STRICT. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details. 
* [Component Location Analysis](runningdetect/component-location-analysis.md) is now available for offline and Rapid/Stateless online scans of NPM, Maven, Gradle and NuGet projects.

### Resolved issues

* (IDETECT-3921) [solution_name] will now validate directory permissions prior to downloading the [solution_name] JAR file.

### Dependency updates

* Upgraded Docker Inspector to version 10.1.0. See the [Docker Inspector Release notes](packagemgrs/docker/releasenotes.md) for further details.

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
