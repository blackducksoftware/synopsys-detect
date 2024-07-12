# Tools

By default, all detection tools are eligible to run; the set of tools that will run
depends on your configuration, type of files you are scanning, and the properties you set.   

When no `--detect.tools=` parameter or the `--detect.tools=ALL` parameter is provided, [company_name] [solution_name] will attempt to run all tools for which the tool itself is available, the configuration parameters are set, and any required dependencies are met. The existence of applicable file types (for scanning), will also determine whether tools return results when they run.   

If you wish to specifically determine which tools are run, use the following command to list the tools:

````
--detect.tools={comma-separated list of tool names in uppercase}
````

To exclude specific tools from execution, use:

````
--detect.tools.excluded={comma-separated list of tool names, all uppercase}
````

<note type="note">Exclusions take precedence over inclusions.</note>

Refer to [Tools](../../components/tools.md) for the list of tool names.

Refer to [Properties](../../properties/all-properties.md) for additional details.

<note type="note">Some [company_name] [solution_name] tools are appropriate to run independantly of others or require a specific license and will not be executed when `--detect.tools=ALL`.</note>
