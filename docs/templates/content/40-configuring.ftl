# Configuring

${solution_name} is configured by assigning values to properties.

## On the command line

One method for configuring ${solution_name} is by setting [${solution_name} property values](../properties/all-properties/) on the command line.
When setting a property value on the command line, prefix the property name with two hyphens (--).

To add one property setting to the command line, add the following at the end:
```
{space}--{property name}={value}
```
There is a space before and between each complete property setting, but there are no spaces around the equals sign (=).

For example,
to set property *detect.project.value*:
```
bash <(curl -s -L https://detect.synopsys.com/detect.sh) --detect.project.name=MyProject
```

## Using environment variables

${solution_name} properties can also be set using environment variables.

On Linux, when setting a property value using an environment variable, the environment variable name
is the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_"). For example:
```
export DETECT_PROJECT_NAME=MyProject
bash <(curl -s -L https://detect.synopsys.com/detect.sh)
```

On Windows, the environment variable name can either be the original property
name, or the property name converted to uppercase with period characters (".") converted to underscore
characters ("_"). For example:
```
$Env:DETECT_PROJECT_NAME = MyProject
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"
```

## Using a configuration file

Another commonly-used method of configuring ${solution_name} is to provide a configuration file. The configuration file
can be a Java properties (.properties) file, or a YAML (.yml) file.

Spring will look for a configuration file named application.properties or application.yml
in the current working directory, or a ./config subdirectory. If it exists, it will read
property values from it.

For example, if you wanted to set property *detect.project.name* using a configuration (.properties) file, you
could do it as follows:
````
echo "detect.project.name=myproject" > application.properties
bash <(curl -s -L https://detect.synopsys.com/detect.sh) --detect.source.path=/opt/projects/project1
````
Because the configuration file has one of the file names that Spring looks for by default
(in this case, application.properties) and exists in one of the locations
that Spring looks in by default (in this case, the current directory), there is no need to specify the path
to the configuration file on the command line.

### Properties file

When setting a property value in a .properties file, do not prefix the property name with hyphens, and adhere to Java .properties
file syntax: `propertyName=propertyValue`, one per line.

### YAML file

When setting a property value in a .yml file, do not prefix the property name with hyphens,
and adhere to YAML syntax for dictionaries: `propertyName: propertyValue`, one per line.

## Switching between multiple profiles

A profile is, in effect, a set of pre-defined properties. You select the profile (property settings)
you want when you run ${solution_name}.

### Creating a profile

To define a set of properties for a profile, create a configuration file named *application-{profilename}.properties*
or *application-{profilename}.yml* in the current working directory, or in a subdirectory named *config*.
Populate it with property assignments as previously described.

### Selecting a profile on the command line

To select one or more profiles on the ${solution_name} command line, assign the the comma-separated list of profiles
to the Spring Boot property *spring.profiles.active*:
```
bash <(curl -s -L https://detect.synopsys.com/detect.sh) --spring.profiles.active={profilename}
```

This capability is provided by Spring Boot. For more information, refer to
[Spring Boot's profile mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-profiles).

## Additional configuration methods and details

${solution_name} reads property values using
[Spring Boot's externalized configuration mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config),
which provides capabilities beyond those described on this page.

The most common methods used to pass a property value to ${solution_name} are listed as follows. A method with lower number in Spring Boot's order of precedence overrides a method with a higher number.

* Using a command line argument (#4 in Spring Boot's order of precedence):
````
--blackduck.url=https://blackduck.yourdomain.com
````
* Using one environment variable per property (#10 in Spring Boot's order of precedence):
````
export BLACKDUCK_URL=https://blackduck.yourdomain.com
````
* Using property assignments in a .properties configuration file (#14 in Spring Boot's order of precedence):
````
blackduck.url=https://blackduck.yourdomain.com
blackduck.api.token=yourtokenvalue
````
* Using property assignments in a .yml configuration file (also #14 in Spring Boot's order of precedence, but .properties takes precedence over .yml):
````
blackduck.url: https://blackduck.yourdomain.com
blackduck.api.token: yourtokenvalue
````
* Using the SPRING_APPLICATION_JSON environment variable with a set of properties set using JSON format (#5 in Spring Boot's order of precedence):
````
export SPRING_APPLICATION_JSON='{"blackduck.url":"https://blackduck.yourdomain.com","blackduck.api.token":"yourgeneratedtoken"}'
````

Refer to the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)
for more details and more sophisticated ways to set properties.

## Providing sensitive values such as credentials

You can provide sensitive values such as credentials to ${solution_name} using a variety of
mechanisms provided by [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config),
including:

* On the command line; for example, --blackduck.password={your password}.
* As an environment variable value; for example, export BLACKDUCK_PASSWORD={your password}.
* In a configuration (.properties) file; for example, ./application.properties.

Values provided on the command line may be visible to other users that can view process details.
Setting sensitive values using environment variables is usually considered more secure.
Connecting to another system; for example, ${blackduck_product_name} or ${polaris_product_name}) using an access token (also called an API token)
is usually considered more secure than connecting using a username and password. 
