# Current Release notes

## Version 9.7.0

### New features
* Support for GoLang is now extended to Go 1.22.2.
* [company_name] [solution_name] now allows exclusion of development dependencies when using the Poetry detector. See the [detect.poetry.dependency.groups.excluded](properties/detectors/poetry.md#detect.poetry.dependency.groups.excluded) property for more information.

### Resolved issues
* (IDETECT-3181) Improved Eclipse component matching implementation through better handling of external identifiers.
* (IDETECT-3989) Complete set of policy violations, regardless of category, now printed to console output. 
