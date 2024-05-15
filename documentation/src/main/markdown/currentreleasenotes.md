# Current Release notes

## Version 9.7.0

### New features
* Support for GoLang is now extended to Go 1.22.2.
* [company_name] [solution_name] now allows exclusion of development dependencies when using the Poetry detector. See the [detect.poetry.dependency.groups.excluded](properties/detectors/poetry.md#detect.poetry.dependency.groups.excluded) property for more information.

* Support has been added for Setuptools versions 47.0.0 to 69.4.2. Scans can be done in both build and buildless mode. The build Detector requires pip. Both modes require a pyproject.toml file with a build section containing a requires = ["setuptools"] or equivalent line. It is recommended that the build Detector be run in a virtual environment, or environment with a clean global pip cache, where a pip install . has been done only for the project being scanned.

### Resolved issues
* (IDETECT-3181) Improved Eclipse component matching implementation through better handling of external identifiers.
