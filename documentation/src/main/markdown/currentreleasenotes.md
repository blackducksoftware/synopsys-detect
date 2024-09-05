# Current Release notes

## Version 10.0.0

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
* Support for Python has now been extended with Pip 24.2, Pipenv 2024.0.1, and Setuptools 74.0.0.
* Correlated Scanning is a new Match as a Service (MaaS) feature which correlates match results from Package Manager (Detector), and Signature scans when running [solution_name] with [blackduck_product_name] 2024.10.0 or later.
	* Correlation between scanning methods increases accuracy and provides for more comprehensive scan results.

