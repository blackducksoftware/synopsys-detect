# Properties

Properties in [solution_name] provide information used to determine how and what actions [solution_name] takes during a scanning run. A property to which you assign a value is like a flag or a parameter on the command line or in a script that provides instructions for the [solution_name] scan task.

When setting a property value, the property name is prefixed with two hyphens (--). 

````
bash <(curl -s -L https://detect.synopsys.com/detect9.sh) <--property=value>
````

Example using properties to specify project name and [blackduck_product_name] URL:

````
bash <(curl -s -L https://detect.synopsys.com/detect9.sh) --detect.project.name=MyProject --blackduck.url=https://blackduck.yourdomain.com
````

<note type="note">When configuring [solution_name] via environment variables or configuration file, specific property handling applies. See [Using environment variables](../../configuring/envvars.md) or [Using a configuration file](../../configuring/configfile.md).</note>
