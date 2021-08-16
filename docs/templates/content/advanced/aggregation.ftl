# BDIO aggregation

## Background

By default, when run on a complex project (with, say, multiple subprojects), ${solution_name} will generate (and upload to ${blackduck_product_name})
multiple BDIO files. Each BDIO file is mapped to a codelocation (scan) in ${blackduck_product_name}. The information
in the codelocation name provides potentially
valuable information about where components originated. These names are visible on the ${blackduck_product_name} Project Version Source tab.

As an alternative, ${solution_name} provides the option (enabled by setting the
*detect.bom.aggregate.name* property) to aggregate those BDIO files into a single BDIO file, which is uploaded
to ${blackduck_product_name} producing a single codelocation name.

## For users of ${blackduck_product_name} 2021.10.0 or later

If you are using ${blackduck_product_name} 2021.10.0 or later and you want to use BDIO aggregation,
we recommend you set detect.bom.aggregate.remediation.mode to SUBPROJECT. This takes advantage of
functionality in newer versions of ${blackduck_product_name} that avoids the issues described below.

## For users of ${blackduck_product_name} older than 2021.10.0

For users of ${blackduck_product_name} older than 2021.10.0,
BDIO aggregation involves tradeoffs,
and it is important to understand those tradeoffs when using it.

When BDIO is aggregated, the source information (subproject name, etc.)
that (in the default non-aggregated scenario) would have appeared in the codelocation names is moved to
top level metadata components, moving the formerly top-level components down a level in the dependency
graph. The downside to this is that *all* components (including direct dependencies) from the aggregated
BDIO appear with dependency type *Transitive* in ${blackduck_product_name}.

In order to preserve the accuracy of the dependency type field in ${blackduck_product_name}, you can
set property *detect.bom.aggregate.remediation.mode* to DIRECT, which tells ${solution_name} to keep
direct dependencies at the top level of the graph. The downside of this approach is that the source information
(which would otherwise be visible on the ${blackduck_product_name} Source tab) is removed.

## Summary

If you are using ${blackduck_product_name} 2021.10.0 or later, set detect.bom.aggregate.remediation.mode to SUBPROJECT.

For users of older ${blackduck_product_name} versions:

* detect.bom.aggregate.remediation.mode=DIRECT provides accurate dependency type values (direct vs. indirect), but loses information about component source.
* detect.bom.aggregate.remediation.mode=TRANSITIVE preserves source information, but results in inaccurate dependency type values.
