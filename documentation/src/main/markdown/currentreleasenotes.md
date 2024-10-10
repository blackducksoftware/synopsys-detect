# Current Release notes

## Version 10.0.0

[company_name] [solution_name] has been renamed [detect_product_long] with page links, documentation, and other URLs updated accordingly. Update any [detect_product_short] documentation, or other bookmarks you may have. See the [Domain Change FAQ](https://community.blackduck.com/s/article/Black-Duck-Domain-Change-FAQ).
* As part of this activity, sig-repo.synopsys.com and detect.synopsys.com are being deprecated. Please make use of repo.blackduck.com and detect.blackduck.com respectively. 
	* After February 2025, [detect_product_short] script download details will only be available via detect.blackduck.com.
	* [detect_product_short] 10.0.0 will only work when using repo.blackduck.com.

<note type="note">It is recommended that customers continue to maintain sig-repo.synopsys.com, and repo.blackduck.com on their allow list until February 2025 when sig-repo.synopsys.com will be fully replaced by repo.blackduck.com.</note>

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
* Support for Python has now been extended with Pip 24.2, Pipenv 2024.0.1, and Setuptools 74.0.0.
* Support for npm has been extended to 10.8.2 and Node.js 22.7.0.
* Support for Maven has been extended to 3.9.9.
* Support for pnpm has been extended to 9.0.
* Support for BitBake is now extended to 2.8.0 (Yocto 5.0.3)
* Support for Nuget has been extended to 6.11.
* Support for GoLang is now extended to Go 1.22.7.
* Correlated Scanning is a new Match as a Service (MaaS) feature which correlates match results from Package Manager (Detector), and Signature scans when running [solution_name] with [blackduck_product_name] 2024.10.0 or later.
	* Correlation between scanning methods increases accuracy and provides for more comprehensive scan results.
	See the [detect.blackduck.correlated.scanning.enabled](properties/configuration/general.html#correlated-scanning-enabled) property for more information
	<note type="note">Correlated Scanning support is available for persistent Package Manager and Signature Scanning only.</note>

### Changed features

* The `logging.level.com.synopsys.integration` property deprecated in [detect_product_short] 9.x, has been removed. Use `logging.level.detect` instead.
* The FULL_SNIPPET_MATCHING and FULL_SNIPPET_MATCHING_ONLY options for the `detect.blackduck.signature.scanner.snippet.matching` property deprecated in [detect_product_short] 9.x, have been removed.

### Dependency updates

* Updated jackson-core library to version 2.15.0 to resolve a security vulnerability.

