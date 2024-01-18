# Current Release notes

## Version 9.3.0

### Changed features

* Any arguments that specify the number of threads to be used provided as part of the `detect.maven.build.command` [solution_name] property will be omitted when executing the Maven CLI.

### Resolved Issues

* (IDETECT-4164) Improved Component Location Analysis parser support for package managers like Poetry that employ variable delimiters, for better location accuracy.
* (IDETECT-4171) Improved Component Location Analysis data validation support for package managers like NPM.
* (IDETECT-4174) Resolved an issue where [solution_name] was not sending the container scan size to [blackduck_product_name] server, resulting in  [blackduck_product_name]'s "Scans" page reporting the size as zero.
* (IDETECT-4176) The FULL_SNIPPET_MATCHING and FULL_SNIPPET_MATCHING_ONLY options, currently controlled via registration key, for the --detect.blackduck.signature.scanner.snippet.matching property will be deprecated in the next major release of [solution_name].

### Dependency updates

* Updated Guava library from 31.1 to 32.1.2 to resolve high severity [CVE-2023-2976](https://nvd.nist.gov/vuln/detail/CVE-2023-2976).