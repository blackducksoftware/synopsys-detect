# Using a configuration file

Another commonly-used method of configuring [solution_name] is to provide a configuration file. The configuration file
can be a Java properties (.properties) file, or a YAML (.yml) file.

Spring Boot will look for a configuration file named application.properties or application.yml
in the current working directory, or a ./config subdirectory. If it exists, it will read
property values from it.

For example, if you wanted to set property *detect.project.name* using a configuration (.properties) file, you
could do it as follows:
````
echo "detect.project.name=myproject" > application.properties
bash <(curl -s -L https://detect.synopsys.com/detect8.sh) --detect.source.path=/opt/projects/project1
````
Because the configuration file has one of the file names that Spring looks for by default
(in this case, application.properties) and exists in one of the locations
that Spring looks in by default (in this case, the current directory), there is no need to specify the path
to the configuration file on the command line.

Additional details can be found in the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/howto.html#howto-externalize-configuration).

## Properties file

When setting a property value in a .properties file, do not prefix the property name with hyphens, and adhere to Java .properties
file syntax: `propertyName=propertyValue`, one per line.

## YAML file

When setting a property value in a .yml file, do not prefix the property name with hyphens,
and adhere to YAML syntax for dictionaries: `propertyName: propertyValue`, one per line.
There is a [solution_name] command line help option, -hyaml, that can be used to generate a template YAML configuration file. 

## Running [solution_name] from a directory that contains a file named *config*

If a file named *config* exists in the directory from which you run [solution_name], 
you must override the default value of the Spring Boot property *spring.config.location* so that Spring Boot does not try to read
that file as a directory. If you are using a Spring Boot configuration
file such as application.properties or application.yml, set the value of *spring.config.location* so Spring Boot will find your configration file.
That is, set it to the path of the directory in which that file resides (include a trailing slash to indicate that you are specifying a directory), or to the path of the config file itself.

If you are not using a Spring Boot configuration file, set the value of *spring.config.location* to the empty string:
````
--spring.config.location=""
````
