# Current Release notes

## Version 9.1.0

### Changed features

* Support for Dart is now extended to Dart 3.1.

### Resolved issues
* (IDETECT-4056) Resolved a problem with CPAN detector where no components were reported.
  Additionally, if cpan command has not been run and configured on the system previously, Detect instructs cpan to accept default configurations. This allows the command to finish executing successfully.