# Properties

A property to which you assign a value is like a flag or a parameter on the command line or in a script that provides instructions for the [solution_name] scan task.

When setting a property value, the property name is prefixed with two hyphens (--). 

````
bash <(curl -s -L https://detect.synopsys.com/detect8.sh) <--property=value>
````

Example using properties to specify project name and [blackduck_product_name] URL:

````
bash <(curl -s -L https://detect.synopsys.com/detect8.sh) --detect.project.name=MyProject --blackduck.url=https://blackduck.yourdomain.com
````
