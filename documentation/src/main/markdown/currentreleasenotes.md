# Current Release notes

## Version 9.10.0

### Changed features

* The `logging.level.com.synopsys.integration` property will be deprecated in favor of the `logging.level.detect` property in [company_name] [solution_name] 10.0.0. 
    <note type="note">There is no functional difference between the two properties.</note>

* Switched from Universal Analytics to Google Analytics 4 (GA4) as our phone home analytics measurement solution. 

* In 9.9.0 the ability to perform multipart uploads for binary scans was added where related properties were not configurable at runtime. As of this release an optional environment variable setting the upload chunk size has been made available. This variable is primarily intended for troubleshooting purposes. See [Environment variables](scripts/overview.md).

### Dependency updates

* Detect Docker Inspector version updated to 10.2.1
