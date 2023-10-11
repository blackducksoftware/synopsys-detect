# Current Release notes

## Version 9.1.0

### Changed features

* Support for Dart is now extended to Dart 3.1.2 and Flutter 3.13.4.
* [solution_name] will now wait for a number of seconds specified by [blackduck_product_name] before attempting to retry creating a new scan when [blackduck_product_name] is busy.

### Resolved issues
* (IDETECT-4056) Resolved an issue where no components were reported by CPAN detector.
  If the cpan command has not been previously configured and run on the system, [solution_name] instructs CPAN to accept default configurations.
* (IDETECT-3843) Additional information is now provided when [solution_name] fails to update and [solution_name] is internally hosted.