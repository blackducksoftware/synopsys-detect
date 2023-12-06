# Tools

By default, all detection tools are eligible to run; the set of tools that will run
depends on your configuration, type of files you are scanning, and the properties you set.   

Use the following command to specifically list the tools that are eligible to run:

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
