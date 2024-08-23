# Current Release notes

## Version 10.0.0

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like `^1.2.0` will now be extracted as `1.2.0` instead of as the raw `^1.2.0` string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.

## Version 9.10.0

### Changed features

* The `logging.level.com.synopsys.integration` property is being removed in [company_name] [solution_name] 10.0.0. Please use the `logging.level.detect` property instead. There is no difference between the two properties.

### Dependency updates

* Detect Docker Inspector version updated to 10.2.1
