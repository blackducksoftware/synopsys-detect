# Current Release notes

## Version 10.0.0

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
* Support for Python has now been extended with Pip 24.2, Pipenv 2024.0.1, and Setuptools 74.0.0.

## Version 9.10.0

### Changed features

* The `logging.level.com.synopsys.integration` property has been deprecated in favor of `logging.level.detect` and will be removed in 10.0.0. 
    <note type="note">There is no functional difference between the two properties.</note>

* Support for GoLang is now extended to Go 1.22.5.

* Switched from Universal Analytics to Google Analytics 4 (GA4) as our phone home analytics measurement solution. 

* In 9.9.0 the ability to perform multipart uploads for binary scans was added where related properties were not configurable at runtime. As of this release an optional environment variable setting the upload chunk size has been made available. This variable is primarily intended for troubleshooting purposes. See [Environment variables](scripts/overview.md).

### Dependency updates

* Detect Docker Inspector version updated to 10.2.1
