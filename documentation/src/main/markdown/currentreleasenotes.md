# Current Release notes

## Version 9.6.0

### New features
* ReversingLabs Scans - this new feature provides analysis of software packages for file-based malware threats.
	See [ReversingLabs Scans](runningdetect/threatintelscan.md) for further information.
* Component Location Analysis upgraded to certify support for location of components in Yarn Lock and Nuget Centralized Package Management files.
* Added support for Gradles rich model for declaring versions, allowing the combination of different levels of version information. See [rich version declarations](packagemgrs/gradle.md#rich-version-declaration-support).

### Resolved issues

* (IDETECT-4211) Resolved an error handling issue with the scan retry mechanism when the git SCM data is conflicting with another already scanned project.
* (IDETECT-4263) Remediated the possibility of [solution_name] sending Git credentials to [blackduck_product_name] Projects API in cases when the credentials are present in the Git URLs.

