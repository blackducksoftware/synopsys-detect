# Current Release notes

## Version 10.0.0

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
* Support for Python has now been extended with Pip 24.2, Pipenv 2024.0.1, and Setuptools 74.0.0.

### Changed features

* The `logging.level.com.synopsys.integration` property that was deprecated in [detect_product_long] 9.x, has been removed. Use `logging.level.detect` instead.
* The FULL_SNIPPET_MATCHING and FULL_SNIPPET_MATCHING_ONLY options for the `detect.blackduck.signature.scanner.snippet.matching` property that was deprecated in [detect_product_long] 9.x, have been removed.
