# Tools

By default, all tools are eligible to run; the set of tools that actually run
depends on the properties you set.
To limit the eligible tools to a given list, use:

--detect.tools={comma-separated list of tool names, all uppercase}

To exclude specific tools, use:

````
--detect.tools.excluded={comma-separated list of tool names, all uppercase}
````

Exclusions take precedence over inclusions.

Refer to [Tools](../../components/tools.md) for the list of tool names.

Refer to [Properties](../../properties/all-properties.md) for details.
