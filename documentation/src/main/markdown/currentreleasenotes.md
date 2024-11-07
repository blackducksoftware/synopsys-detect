# Current Release notes

## Version 10.1.0

### New features

* npm lockfile and shrinkwrap detectors now ignore packages flagged as extraneous in the package-lock.json and npm-shrinkwrap.json files.

### Changed features

* npm version 1 package-lock.json and npm-shrinkwrap.json file parsing has been restored.
* The `detect.project.codelocation.unmap` property has been deprecated.

### Resolved issues

* (IDETECT-4517) - [detect_product_short] now correctly indicates a timeout failure occurred when multipart binary or container scans timeout during an upload.
* (IDETECT-4540) - multipart binary and container scans now correctly retry when authentication errors are received during transmission.