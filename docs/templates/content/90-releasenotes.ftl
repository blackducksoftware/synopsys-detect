# Release notes

## Version 6.2.0
### New features
* The ${solution_name} .jar file is now signed, enabling [code verification](/advanced/verifying/) by users.
* [Simple proxy information](/advanced/language-and-package-managers/gradle/#running-the-gradle-inspector-with-a-proxy) will be forwarded to the Gradle Inspector.
* Detect now creates a status file describing the results of the run which includes things like [issues, results and status codes.](advanced/status-file.md)
* Property configuration table includes where property was resolved.
* Added the property [detect.blackduck.signature.scanner.license.search](properties/Configuration/signature scanner.md#signature-scanner-license-search-advanced).
* Added the property [detect.blackduck.signature.scanner.individual.file.matching](properties/Configuration/signature scanner.md#individual-file-matching-advanced).
* If an executable returns a nonzero exit code, Detect will now log output automatically.
* Added page for [decrecated properties](../properties/deprecated-properties/) in help.
* Detect-generated risk reports now feature Synopsys logo and branding.

### Changed features
* The [PipEnv Detector](/advanced/language-and-package-managers/python/#pipenv-detector) now parses a json representation of the dependency tree.
* Powershell download speed increased.

### Resolved issues
* Resolved an issue where the download URL for ${solution_name} was being set to an internal URL upon release.
* Resolved an issue where all transitive dependencies found by the [Pip inspector](/advanced/language-and-package-managers/python/#the-pip-detector) were being reported as direct dependencies.
* Resolved an issue where using pip version 20+ with the [Pip inspector](/advanced/language-and-package-managers/python/#the-pip-detector) caused a failure to import a dependency. [GitHub PR](https://github.com/blackducksoftware/synopsys-detect/pull/107)
* Resolved the following vulnerabilities:
    * org.springframework.boot:spring-boot-starter 5.1.7.RELEASE BDSA-2020-0069 (CVE-2020-5398)
* Resolved an issue where ${solution_name} had the potential to fail on projects that utilized Yarn workspaces.
    * Note: Yarn workspaces are not currently supported. See [yarn workspace support](/advanced/language-and-package-managers/yarn/#yarn-workspace-support).
* When parsing package.xml files, Detect will no longer raise a SAXParseException when the file contains a doctype declaration, and will continue parsing the rest of the file.
* Resolved an issue in the Bazel Detector that caused it to fail for the maven_install rule when the tags field contained multiple tags with a mixture of formats.
* Resolved an issue that could cause generation of an invalid Black Duck Input/Output (BDIO) file when component the only difference between two component names/versions are non-alphanumeric characters.

## Version 6.1.0
### New features
* Added the property [detect.bdio2.enabled](properties/Configuration/paths.md#bdio-2-enabled).
* Added the property [detect.pip.only.project.tree](properties/Detectors/pip.md#pip-include-only-project-tree).
* Added the property [detect.bitbake.search.depth](properties/Detectors/bitbake.md#bitbake-search-depth).
* Added the property [detect.bazel.cquery.options](properties/Detectors/bazel.md#bazel-cquery-additional-options).
* Added the property [detect.docker.image.id](properties/Detectors/docker.md#docker-image-id).
* Added the property [detect.docker.platform.top.layer.id](properties/Detectors/docker.md#platform-top-layer-id-advanced).
* Added the property [detect.bom.aggregate.remediation.mode](properties/Configuration/project.md#bdio-aggregate-remediation-mode-advanced)

### Changed features
* Deprecated all ${polaris_product_name}-related properties.
* Added [wildcard support](advanced/includeexcludewildcards.md) for several include/exclude list properties.
* Improved the structure of the dependency information produced by the Yarn detector by changing its approach. It now parses dependency information from yarn.lock and package.json, instead of running the yarn command. Since the yarn command is no longer executed, the detect.yarn.path property has been removed.
* Improved match accuracy for Bitbake projects by improving external ID generation for dependencies referenced using Git protocols, and dependencies referenced with an epoch and/or revision.
* Improved the reliability of the Bitbake detector by generating recipe-depends.dot and package-depends.dot files the source directory, instead of a temporary directory.
* Changed the logging level of ${polaris_product_name} CLI output from DEBUG to INFO.
* Added support for the Noto-CJK font (for Chinese, Japanese, and Korean text) in the risk report.

### Resolved issues
* Resolved an issue that can cause a Null Pointer Exception on Maven projects configured for multi-threaded builds.
* Resolved an issue that can cause Detect to fail due to an expired Black Duck bearer token.
* Resolved an issue that causes Detect to fail when a parent project and version are specified, and the project is already a child of the specified parent.
* Resolved an issue that causes Detect to log the git username and password when a git command executed by Detect fails.
* Resolved an issue that can cause Detect to generate a new code location (scan) when the character case of the value of the detect.source.path property differs from a previous run on the same project.
* Resolved the following vulnerabilities: commons-beanutils:commons-beanutils 1.9.3 / BDSA-2014-0129 (CVE-2019-10086), org.apache.commons:commons-compress 1.18 / BDSA-2019-2725 (CVE-2019-12402)

## Version 6.0.0
### New features
* Added the property detect.binary.scan.file.name.patterns.
* Added the property detect.detector.search.exclusion.files which accepts a comma-separated list of file names to exclude from the Detector search.
* Custom arguments for the source command can now be supplied to Detect through the property detect.bitbake.source.arguments which accepts a comma-separated list of arguments. (1614)
* Added support for the Swift package manager.
* Added support for GoGradle.
* Added support for Go Modules.
* The property detect.pip.requirements.path is now a comma-separated list of paths to requirements.txt files. This enables you to specify multiple requirements files. Each requirements file displays as a new code location in Black Duck.
* Detect now logs username, roles, and groups for the current user.
* Detect now includes the project name/version in every code location name.
* Detect now takes in a go path, but does not take in go.dep.path; nor does Detect trigger on *.go.
* The property detect.parallel.processors is added. This property controls the number of parallel threads, and replaces the properties detect.blackduck.signature.scanner.parallel.processors and detect.hub.signature.scanner.parallel.processors.
* Added the property detect.maven.included.scopes. This is a comma-separated list of Maven scopes. Output is limited to dependencies within these scopes, and is overridden by exclude.
* Added the property detect.maven.excluded.scopes. This is a comma-separated list of Maven scopes. Output is limited to dependencies outside these scopes, and is overridden by include.
* Bazel detector: added support for dependencies specified using the maven_install workspace rule.  The detect.bazel.advanced.rules.path property is removed.
* When using Detect for static analysis, you can pass the build command to let the Polaris CLI know how to analyze a given project.

### Changed features

* Architecture is no longer included in BitBake dependencies discovered by Detect. The property detect.bitbake.reference.impl is no longer used and is deprecated.
* The BitBake detector no longer uses the property detect.bitbake.reference.impl because architecture is no longer required to match with artifacts in the KnowledgeBase. The Bitbake detector now attempts to determine the layer in which a component originated instead of the architecture.
* Improved the Detect on-screen logging to be more concise.
* The PiP inspector is no longer deprecated, and is currently supported.
* When creating an air gap zip of Detect using the switch -z or --zip, the created zip file is now published to your output directory.
* Scripts no longer fail if the Artifactory server is unavailable.
* Enhanced placement and formatting of deprecation logs.
* Added support for Java version 11.
* The following properties are removed in Detect version 6.0.0:
* detect.go.dep.path
* detect.npm.node.path
* detect.perl.path
* detect.go.run.dep.init
* detect.maven.scope
* detect.bazel.advanced.rules.path

### Resolved issues

* Resolved an issue wherein the Windows Java path construction did not account for direction of the slash. The shell script now uses the correct slash direction, based on the operating system on which Detect is running.
* Resolved an issue wherein Detect was not finding the file recipe-depends.dot written to the current directory. Detect now looks in the source directory to a depth of 1 if it cannot find the expected files in the expected location.
* Resolved an issue wherein Detect was failing if it could not resolve placeholders.
* Resolved an issue wherein Detect was not handling SSH URLs, which caused Detect to fail in extracting project information from the Git executable. GitCliDetectable now properly handles SSH URLs.
* Resolved an issue wherein the Detect JAR was downloading for each scan when the script could not communicate with Artifactory. Now, if the script cannot communicate with Artifactory, and there is an existing downloaded Detect, then the previously-downloaded version of Detect runs. However, if you provided a DETECT_LATEST_RELEASE_VERSION and Detect cannot communicate with Artifactory, Detect will not run.
* Resolved an issue wherein Detect was not properly parsing GIT URLs such as git://git.yoctoproject.org/poky.git.

## Version 5.6.2

### Resolved issues

* Synopsys Detect version 5.6.2 is a rebuild of version 5.6.0 and 5.6.1 to address an issue with the binary repository to which it was published.

## Version 5.6.0

### New features

* You can now set custom fields on created Black Duck projects.
* Detect can now generate its own air gap zip.
* Detectors now nest by default.
* Added support for Gradle Kotlin.
* Added support for wildcard (*) in the Detect flag blackduck.proxy.ignored.hosts.
* Added support for --detect.project.tags.
* Added the properties --detect.parent.project.name and --detect.parent.project.version.name.
* Added the property  --detect.clone.project.version.latest=true which takes precedence over the exact version name.
* Added support for Yocto 2.0.0.
* Added support to parse components from the &lt;plugins&gt; block in pom.xml. This only works when detect.detector.buildless=true.
* Added capability to represent '' and "" as a null value in Detect multiselect custom fields.

### Changed features

* You can now specify the search depth for buildless mode.
* Updated the help menu and provided more detailed help options.
* Diagnostics now includes signature scanner log files.
* Re-enabled empty aggregate file generation.
* Polaris no longer runs the the -w switch enabled by default.  To retrieve the issue/policy count, you can use the -w switch.
* Match accuracy for Docker images is improved by running the signature scanner on a squashed version of the Docker image instead of the container file system. This results in a different name for the code location because the name of the file being scanned is different. For existing projects, the old code location named by default as &lt;repo&gt;_&lt;tag&gt;_containerfilesystem.tar.gz/&lt;repo&gt;/&lt;tag&gt; scan must be removed to ensure it does not contribute stale data to the BOM. Due to the new method of scanning, the code location name has changed.  You must remove the old code location in favor of the new code location.

### Resolved issues

* Resolved an issue that could cause code location names to contain relative file paths when the value of detect.source.path uses symbolic links to specify the source directory.
* Resolved an issue that caused detect.sh to fail when Java is not on the system path, and the JAVA_HOME path contains a space.
* Resolved an issue wherein the signature scanner may not have been reporting failures correctly.
* Resolved an issue wherein Detect was not locating the file recipe-depends.dot when it was written to the current directory. Detect now searches for the recipe-depends.dot file to a depth of 1 when extracting on a BitBake project.
* Detect no longer fails if the Git executable is not found.
* Resolved an issue wherein Detect may fail when the directory pointed to by --detect.notices.report.path does not exist.

## Version 5.5.1

### Resolved issues

* Resolved an issue wherein the Pipenv detector was omitting project dependencies.

## Version 5.5.0

### New features

* Added support for snippet modes.
* The property detect.wait.for.results is been added to wait for Black Duck.  The default value is false.  If this property is set to true, Detect won't complete until the normal timeout is reached or the underlying systems with which Detect is communicating are once again idle and ready to receive more data.  The timeout value is controlled by blackduck.timeout.
* The shell script and PowerShell script now accept DETECT_JAVA_PATH and DETECT_JAVA_HOME as environment variables for pointing to your Java installation.
* Added a new property --detect.detector.search.exclusion.paths.  A comma-separated list of directory paths to exclude from a detector search. For example, foo/bar/biz only excludes the biz directory if the parent directory structure is 'foo/bar/'.
* Detect now uses Git information to determine the default project and version names.
* There is a new Detect property for overriding the Git executable: detect.git.path.

### Resolved issues


* Resolved an issue that caused the risk report to be generated with invalid links to Black Duck components.
* Resolved an issue that caused a null pointer exception error when a golang's Gopkg.lock file contained zero projects.
* Resolved an issue wherein the Clang detector could omit the epoch from the version string in RPM packages.
* Resolved an issue wherein with two users running Detect on a single system may result in a Permission denied error.
* Resolved an issue wherein the property -detect.policy.check.fail.on.severities may not be waiting for the snippet scans to complete.
* Resolved an issue wherein the property --detect.blackduck.signature.scanner.exclusion.name.patterns may not be following the paths.
* Resolved an issue wherein Detect may fail when the directory specified by --detect.risk.report.pdf.path did not exist.  Detect now attempts to create the directory structure to the specified path. A warning is logged if Detect fails to create the directory.
* Resolved an issue wherein properties that had a primary group and additional property group may have been excluded from the group search.
* Resolved an issue wherein the deprecation warning displayed when the deprecated property was provided by the user.
* Resolved an issue with aggregate BOM filename generation that could cause the message Unable to relativize path, full source path will be used to display in the log.
* Resolved an issue that could cause components to be omitted from the BOM for Conda projects.
* Resolved an issue that could cause errors during parsing of Maven projects with long sub-project names.

### Changed features

* The default value for the property detect.docker.path.required is now false.
* The ALL logging level is replaced with the TRACE logging level.
* The results URL for the Black Duck project BOM is now moved to the Detect Results panel.
* Renamed Detect Results to Detect Status.
* Previously, a temp file remained which could contain plain-text user name or password information.  This temp file is now removed.
* Bazel is added as an acceptable value to the detect.tools properties.
* Detect now uses the current version of Docker Inspector.  This means that no matter what version of Docker Inspector is currently released, Detect now uses that version.

## Version 5.4.0

### New features

* Added buildless mode.
* Added a new property for BitBake to remove Yocto reference implementation characters.
* Added a new property for adding group names to projects.
* Added a new property for uploading source files.
* Added the additional_components placeholder.

### Resolved issues

* Resolved an issue wherein Yarn may have been incorrectly calculating the tree level.
* Resolved an issue wherein Detect may fail when Polaris is excluded, a Polaris URL is provided, and connection to Polaris failed.
* Resolved an issue that caused Detect to follow symbolic links while searching directories for files.
* Resolved an issue wherein Detect was not failing policy for UNSPECIFIED when fail on severities is set to ALL.
* Resolved an issue that could cause a counter (an integer intended to ensure uniqueness), to be unnecessarily appended to a code location name.
* Resolved an issue that may have caused the package manager name to be excluded from the code location name when a code location name was provided.
* Resolved an issue that could cause Detect to continue after a Polaris connection failure.
* Resolved an issue wherein the Detect scan results may incorrectly show development dependencies.
* Resolved an issue that could cause reports to fail due to timeout intermittently.
* Resolved an issue that could cause the value of --polaris.access.token to be logged to the console when detect.sh is invoked.
* Resolved an issue wherein Detect was cleaning up the contents but not the directory of the run.

### Changed features

* For getting all logs, the ALL logging level is now TRACE.
* Improved the error message logged when the property detect.binary.scan.file.path, which must point to a readable file, points to something other than a readable file, such as a directory.
* Changed the environment variable used to tell the Detect scripts where to download the Detect jar.  The previous value DETECT_JAR_PATH is now changed to DETECT_JAR_DOWNLOAD_DIR.
* Improved the parsing of packrat.lock files to better represent the relationships between dependencies in the graph.
* The version of Detect is no longer part of the code location name.

## Version 5.3.3

* Resolved an issue wherein reports for projects containing risks may be generated with a status of zero risks shown.

## Version 5.3.2

* Synopsys Detect version 5.3.2 is a minor maintenance release.

## Version 5.3.1

### New features

* Added new property detect.ignore.connection.failures which enables Synopsys Detect to continue even if it fails to talk to Black Duck.

### Resolved issues

* Resolved an issue wherein build scan failures may occur in TFS with the error [COPY Operation] noSuchPath in source, path provided: //license/ownership.
* Resolved an issue wherein if the property detect.clone.project.version.name is set to a non-existent project version, the log messages are now improved to make it easier to recognize the problem.

### Changed features

* In cases where the property detect.clone.project.version.name is set to a non-existent project version, the log messages are now improved to make it easier to recognize the issue.

## Version 5.2.0

### New features

* Added support for Bazel.
* Added support for CMake.
* Added a property to support using project version nicknames.
* Added a property for application ID.
* Added Java wildcard pattern support.
* Added support for Coverity on Polaris.

### Resolved issues

* Resolved an issue wherein the package-lock.json file may be missing additional versions.
* Resolved an issue wherein multiple simultaneous Detect executions may cause BDIO merges.
* Resolved an issue wherein permission errors may display when creating projects or scanning.

### Changed features

* The --detect.bom.aggregate.name property now checks for an empty BOM.  If the BOM is empty, it is not uploaded to Black Duck.
* Added support for PiP versions 6.0.0 and higher.
* Improved error messages for Black Duck connection issues.
* Cosmetic changes: from Black Duck Detect to Synopsys Detect.
* Streamlined execution of Coverity and Black Duck scans through a single continuous integration job.
* Updated location of the shell/PowerShell scripts.
* Updated location of the air-gapped archive.

## Version 5.1.0

### New features

* Added support for GoVendor.
* Added executable output to diagnostic mode.
* Added the project/version GUID in the console output.
* Added error codes.

### Resolved issues

* Resolved an issue that fixes the Clang Detector (for C/C++) handling of complex quoted strings occurring in compiler commands found in the JSON compilation database (compile_commands.json) file.
* Resolved an issue wherein a Null Pointer Exception error may occur when Detect cannot access a file during signature scan exclusion calculating.
* Resolved an issue wherein the RubyGems package manager had missing components.
* Resolved an issue wherein the NPM package lock added every dependency as a root dependency.

### Changed features

* The properties --detect.nuget.path and --detect.nuget.inspector.name are deprecated.
* The properties detect.suppress.results.output and detect.suppress.configuration.output are deprecated.  The output from these properties is logged instead of written to sysout.
* Improved the reporting of scan registration limit errors.

## Version 5.0.1

### Resolved issues

* Resolved an issue wherein a null pointer exception error may occur in the NuGet portion of a scan when running Synopsys Detect in Linux.
* Resolved an issue that fixes the Clang Detector (for C/C++) handling of complex quoted strings occurring in compiler commands found in the JSON compilation database (compile_commands.json) file.
* Resolved an issue wherein using detect.tools=ALL did not run any tools.
* Resolved an issue wherein Coverity on Polaris may return a failure status for a successful upload.

### Changed features

* NuGet air gap mode now points to other folders.
* Removed support for PiP resolving the project version.

## Version 5.0.0

### New features

* Added a new property to execute Black Duck Docker Inspector.
* CocoaPods are now nestable under Bill of Materials (BOM) tools.
* Added functionality to exclude all BOM tools.
* Added a new property which enables you to search at a determined depth.
* Added functionality to log all found executables.
* Added functionality to run in Docker mode.
* Added support for NuGet in MacOS.
* Added ability to include and exclude all tools.
* Added a new properties for SWIP in Detect scans.

### Resolved issues

* Resolved an issue that caused the Gradle inspector to retrieve the maven-metadata.xml file from the default repository, even when the property detect.gradle.inspector.repository.url was set to point to a different repository.
* Resolved an issue wherein Gradle may upload older BDIO files into the current project.

### Changed features

* Improved C/C++ multi-threading functionality.
* Deprecated Pipenv inspector messages are now logged.
* The term BOM_TOOL is now replaced with DETECTOR.
* You can no longer supply ranges for the Inspector versions.
* Enhanced the code location naming conventions.
