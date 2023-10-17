# Current Release notes

## Version 9.1.0

### New features

* Container Scan. Providing component risk detail analysis for each layer of a container image, (including non-Linux, non-Docker images). Please see [Container Scan ](runningdetect/containerscanning.md) for details.
	<note type="restriction">Your [blackduck_product_name] server must have [blackduck_product_name] Binary Analysis (BDBA) or [blackduck_product_name] Secure Container (BDSC) licensed and enabled.</note>

### Changed features

* Support for Dart is now extended to Dart 3.1.2 and Flutter 3.13.4.
* When [blackduck_product_name] is busy, [solution_name] will now wait the number of seconds specified by [blackduck_product_name] before attempting to retry scan creation.

### Resolved issues
* (IDETECT-4056) Resolved an issue where no components were reported by CPAN detector.
  If the cpan command has not been previously configured and run on the system, [solution_name] instructs CPAN to accept default configurations.
* (IDETECT-3843) Additional information is now provided when [solution_name] fails to update and [solution_name] is internally hosted.