# Current Release notes

## Version 10.0.0

[company_name] [solution_name] has been renamed [detect_product_long]. As such, page links and documentation URLs have been updated accordingly. Update any [solution_name] documentation bookmarks you may have.

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
* Support for Python has now been extended with Pip 24.2, Pipenv 2024.0.1, and Setuptools 74.0.0.
* Support for npm has been extended to 10.8.2 and Node.js 22.7.0.
* Support for Maven has been extended to 3.9.9.
* Support for pnpm has been extended to 9.0.
* Support for BitBake is now extended to 2.8.0 (Yocto 5.0.3)
* Support for Nuget has been extended to 6.11.
* Correlated Scanning is a new Match as a Service (MaaS) feature which correlates match results from Package Manager (Detector), and Signature scans when running [solution_name] with [blackduck_product_name] 2024.10.0 or later.
	* Correlation between scanning methods increases accuracy and provides for more comprehensive scan results.
	See the [detect.blackduck.integrated.matching.enabled](properties/configuration/general.html#integrated-matching-enabled) property for more information

### Changed features

* The `logging.level.com.synopsys.integration` property that was deprecated in [detect_product_long] 9.x, has been removed. Use `logging.level.detect` instead.
* The FULL_SNIPPET_MATCHING and FULL_SNIPPET_MATCHING_ONLY options for the `detect.blackduck.signature.scanner.snippet.matching` property that was deprecated in [detect_product_long] 9.x, have been removed.
