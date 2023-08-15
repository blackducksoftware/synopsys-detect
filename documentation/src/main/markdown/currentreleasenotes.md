# Current Release notes

## Version 9.0.0

### Changed features

* The `detect.diagnostic.extended` property, that was deprecated in [solution_name] 8.x, has been removed. `detect.diagnostic` can be used instead.
* The Ephemeral Scan Mode, that was deprecated in [solution_name] 8.x, has been removed in favor of Stateless Scan Mode. See the [Stateless Scans page](runningdetect/statelessscan.md) for further details.
* npm 6, which was deprecated in [solution_name] 8.x, is no longer supported.
* [solution_name] 7.x has entered end of support. See the [Product Maintenance, Support, and Service Schedule page](https://sig-product-docs.synopsys.com/bundle/blackduck-compatibility/page/topics/Support-and-Service-Schedule.html) for further details.
* (IDETECT-3879) The detectors\[N\].statusReason field of the status.json file will now contain the exit code of the detector subprocess command in cases when the code is non-zero.
* In addition to node_modules, bin, build, .git, .gradle, out, packages, target, the Gradle wrapper directory `gradle` will be excluded from signature scan by default. Use
  [detect.excluded.directories.defaults.disabled](properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced) to disable these defaults.
* (IDETECT-3626) The NuGet Inspector now resolves dependency versions according to NuGet's 'direct dependency wins' rule.


### Resolved issues

* (IDETECT-3821) Detect will now capture and record failures of the Signature Scanner due to command lengths exceeding Windows limits. This can happen with certain folder structures when using the `detect.excluded.directories` property.
* (IDETECT-3820) Introduced an enhanced approach to NuGet Inspector for handling different formats of the `project.json` file, ensuring compatibility with both old and new structures.
