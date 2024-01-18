# Current Release notes

## Version 9.3.0

### New features

### Changed features

* Any arguments that specify the number of threads to be used provided as part of the `detect.maven.build.command` [solution_name] property will be omitted when executing the Maven CLI.

### Resolved Issues

* (IDETECT-4174) Resolved an issue where [solution_name] was not sending the container scan size to [blackduck_product_name] server, resulting in  [blackduck_product_name]'s "Scans" page reporting the size as zero.
* (IDETECT-4176) The FULL_SNIPPET_MATCHING and FULL_SNIPPET_MATCHING_ONLY options, currently controlled via registration key, for the --detect.blackduck.signature.scanner.snippet.matching property are deprecated and will be removed in the next major release of [solution_name].

### Dependency updates
