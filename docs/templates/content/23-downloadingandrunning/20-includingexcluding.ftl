## Including and excluding tools and detectors

[Properties](../properties/all-properties/) provide a variety of additional options for configuring ${solution_name} behavior. One of the
most fundamental ways to modify ${solution_name} is by including and excluding [tools](../components/tools/) and [detectors](../components/detectors/).

### Tools

By default, all tools are eligible to run; the set of tools that actually run
depends on the properties you set.
To limit the eligible tools to a given list, use:

--detect.tools={comma-separated list of tool names, all uppercase}

To exclude specific tools, use:

````
--detect.tools.excluded={comma-separated list of tool names, all uppercase}
````

Exclusions take precedence over inclusions.

Refer to [Tools](../components/tools/) for the list of tool names.

Refer to [Properties](../properties/all-properties/) for details.

### Detectors

By default, all detectors are eligible to run.  The set of detectors that actually
run depends on the files existing in your project directory.
To limit the eligible detectors to a given list, use:

````
--detect.included.detector.types={comma-separated list of detector names}
````

To exclude specific detectors, use:

````
--detect.excluded.detector.types={comma-separated list of detector names}
````

Exclusions take precedence over inclusions.

Refer to [Detectors](../components/detectors/) for the list of detector names.

Refer to [Properties](../properties/all-properties/) for details.
