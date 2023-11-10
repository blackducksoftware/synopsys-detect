# Current Release notes

## Version 9.2.0

### New features

* Support for pnpm is now extended to 8.9.2

### Changed features

* pnpm 6, and pnpm 7 using the default v5 pnpm-lock.yaml file, is being deprecated. Support will be removed in [solution_name] 10.

## Version 9.1.0

### Changed features

* Support for Dart is now extended to Dart 3.1.2 and Flutter 3.13.4.
* When [blackduck_product_name] is busy, [solution_name] will now wait the number of seconds specified by [blackduck_product_name] before attempting to retry scan creation.

### Resolved issues
* (IDETECT-4056) Resolved an issue where no components were reported by CPAN detector.
  If the cpan command has not been previously configured and run on the system, [solution_name] instructs CPAN to accept default configurations.
* (IDETECT-3843) Additional information is now provided when [solution_name] fails to update and [solution_name] is internally hosted.