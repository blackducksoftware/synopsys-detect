# Usage metrics collection

[solution_name] uses Google Analytics to collect anonymized usage metrics through a mechanism called *phone home*.
[company_name] uses this data to help set engineering priorities.

In a network where access to outside servers is limited, this mechanism may fail, and those failures
may be visible in the log. This is a harmless failure; [solution_name] will continue to function
normally.

To disable this mechanism for [solution_name] runs executed from one environment,
set the environment variable *SYNOPSYS_SKIP_PHONE_HOME* to *true*.
To disable this mechanism for all [solution_name] runs against a specific [blackduck_product_name]
server, refer to the [blackduck_product_name] documentation for information on disabling analytics.