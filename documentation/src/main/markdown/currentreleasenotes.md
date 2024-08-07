# Current Release notes

## Version 10.0.0

### New features

* The npm package.json detector now performs additional parsing when attempting to find dependency versions. This can result in additional matches since versions like 1.2.0 will now be extracted as 1.2.0 instead of as the raw 1.2.0 string. In the case where multiple versions for a dependency are discovered, the earliest version will be used.
