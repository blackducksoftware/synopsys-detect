# BDIO aggregation

## Background

By default, when run on a complex project (with, say, multiple subprojects), ${solution_name} will generate (and upload to ${blackduck_product_name})
multiple BDIO files. Each BDIO file is mapped to a codelocation (scan) in ${blackduck_product_name}. The information
in the codelocation name provides potentially
valuable information about where components originated. These names are visible on the ${blackduck_product_name} Project Version Source tab.

## Options and tradeoffs

As an alternative, ${solution_name} provides the option (enabled by setting the
*detect.bom.aggregate.name* property) to aggregate those BDIO files into a single BDIO file, which is uploaded
to ${blackduck_product_name} producing a single codelocation name.

BDIO aggregation involves tradeoffs,
and it is important to understand those tradeoffs when using it.

When BDIO is aggregated, the source information (subproject name, etc.)
that (in the default non-aggregated scenario) would have appeared in the codelocation names is moved to
top level metadata components, moving the formerly top-level components down a level in the dependency
graph. The downside to this is that *all* components (including direct dependencies) from the aggregated
BDIO appear with dependency type *Transitive* in ${blackduck_product_name}. (A fix for this problem
is planned for a future release.)

In order to preserve the accuracy of the dependency type field in ${blackduck_product_name}, you can
set property *detect.bom.aggregate.remediation.mode* to DIRECT, which tells ${solution_name} to keep
direct dependencies at the top level of the graph. The downside of this approach is that the source information
(which would otherwise be visible on the ${blackduck_product_name} Source tab) is removed.

## Summary

* detect.bom.aggregate.remediation.mode=DIRECT provides accurate dependency type values (direct vs. indirect), but loses information about component source.
* detect.bom.aggregate.remediation.mode=TRANSITIVE preserves source information, but results in inaccurate dependency type values.
* A fix is planned for a future release that will enable preservation of source information while providing accurate dependency type values.
