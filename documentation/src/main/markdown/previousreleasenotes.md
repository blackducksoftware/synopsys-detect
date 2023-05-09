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
