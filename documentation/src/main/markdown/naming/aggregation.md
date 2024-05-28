# BDIO aggregation

Starting with version 8.0.0, [company_name] [solution_name] aggregates all package manager results into a single BDIO file / codelocation.

All dependency graphs produced by any of the following, executed during the [company_name] [solution_name] run, will be aggregated:

* Detectors
* Docker Inspector
* Bazel

This BDIO takes advantage of
functionality added to [blackduck_product_name] in version 2021.8.0
enabling [blackduck_product_name] to preserve both source information (indicating, for example, from which
subproject a dependency originated) and match type information (direct vs. transitive dependencies).

[company_name] [solution_name] now operates in a way that is similar to [company_name] [solution_name] 7
run with property detect.bom.aggregate.remediation.mode=SUBPROJECT.
The property detect.bom.aggregate.remediation.mode does not exist in [company_name] [solution_name] 8.

## Related properties

* [detect.bdio.output.path](../properties/configuration/paths.md#bdio-output-directory)
* [detect.bdio.file.name](../properties/configuration/paths.md#bdio-file-name)

