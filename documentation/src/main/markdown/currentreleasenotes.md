# Current Release notes

## Version 9.7.0

### New features
* Support for GoLang is now extended to Go 1.22.2.
* [company_name] [solution_name] now allows exclusion of development dependencies when using the Poetry detector. See the [detect.poetry.dependency.groups.excluded](properties/detectors/poetry.md#detect.poetry.dependency.groups.excluded) property for more information.
* Support has been added for Python package detection via [Setuptools](https://setuptools.pypa.io/en/latest/index.html),versions 47.0.0 through 69.4.2. See the [Python Package Managers](packagemgrs/python.md) page for further details.

* Support has been added for Python package detection via [Setuptools](https://setuptools.pypa.io/en/latest/index.html),versions 47.0.0 through 69.4.2. See the [Python Package Managers](packagemgrs/python.md) page for further details.

### Resolved issues
* (IDETECT-4341) The Poetry detector will now recognize Python components with case insensitivity.
* (IDETECT-3181) Improved Eclipse component matching implementation through better handling of external identifiers.
* (IDETECT-3989) Complete set of policy violations, regardless of category, now printed to console output. 
