# Current Release notes

## Version 9.9.0

### New features

* [solution_name] now supports binary scanning of large files via a chunking method employed during upload. Testing has confirmed successful upload of 20GB files.
    <note type="note">This feature requires [blackduck_product_name] 2024.7.0 or later.</note>

### Changed features

* When running [company_name] [solution_name] against a [blackduck_product_name] instance of version 2024.7.0 or later, the Scan CLI tool download will use a new format for the URL. 
	* Current URL format: https://<BlackDuck_Instance>/download/scan.cli-macosx.zip
	* New URL format: https://<BlackDuck_Instance>/api/tools/scan.cli.zip/versions/latest/platforms/macosx
* Support for GoLang is now extended to Go 1.22.5.

### Resolved issues

* (IDETECT-4408) - Remediated vulnerability in Logback-Core library to resolve high severity issues [CVE-2023-6378](https://nvd.nist.gov/vuln/detail/CVE-2023-6378) and [CVE-2023-6481](https://nvd.nist.gov/vuln/detail/CVE-2023-6481).

### Dependency updates

* Component Location Analysis version updated to 1.1.13
* Project Inspector version updated to 2024.9.0
* Logback Core version updated to 1.2.13
