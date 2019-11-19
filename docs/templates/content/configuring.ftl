# Configuring

The primary means for configuring ${solution_name} is by setting [${solution_name} property values](properties/all-properties.md).

${solution_name} reads property values using
[Spring Boot's externalized configuration mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config).

The most common methods used to pass a property value to ${solution_name} are listed below. A method with lower number in Spring Boot's order of precedence will
override a method with a higher number.

* Using a command line argument (#4 in Spring Boot's order of precedence)
````
    --blackduck.url=https://blackduck.yourdomain.com
````
* Using one environment variable per property (#10 in Spring Boot's order of precedence)
````
    export BLACKDUCK_URL=https://blackduck.yourdomain.com
````
* Using property assignments in a .properties configuration file (#14 in Spring Boot's order of precedence)
````
    blackduck.url=https://blackduck.yourdomain.com
    blackduck.api.token=yourtokenvalue
````
* Using property assignments in a .yml configuration file (also #14 in Spring Boot's order of precedence, but .properties takes precedence over .yml)
````
    blackduck.url: https://blackduck.yourdomain.com
    blackduck.api.token: yourtokenvalue
````
* Using the SPRING_APPLICATION_JSON environment variable with a set of properties set using JSON format (#5 in Spring Boot's order of precedence)
````
    export SPRING_APPLICATION_JSON='{"blackduck.url":"https://blackduck.yourdomain.com","blackduck.api.token":"yourgeneratedtoken"}'
````
When setting a property value on the command line, prefix the property name with two hyphens ("--"). When setting a property
value in a .properties file, do not prefix the property name with hyphens (and adhere to Java .properties
file syntax: propertyName=propertyValue, one per line).

On Linux, when setting a property value using an environment variable, the environment variable name
is the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_").

On Windows, the environment variable name can either be the original property
name, or the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_").

The most common location for a configuration (.properties or .yml) file is in a file named application.properties or application.yml
in the current working directory, or a ./config subdirectory.

Refer to the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)
for more details and more sophisticated ways to set properties.

## Providing sensitive values such as credentials

You can provide sensitive values such as credentials to ${solution_name} using a variety of
mechanisms provided by [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config),
including:

* On the command line (for example: --blackduck.password={your password})
* As an environment variable value (for example: export BLACKDUCK_PASSWORD={your password})
* In a configration (.properties) file (for example: ./application.properties)

Values provided on the command line may be visible to other users that can view process details.
Setting sensitive value using environment variables is usually considered more secure.
Connecting to another system (e.g. ${blackduck_product_name} or ${polaris_product_name}) using an access token (also called an API token)
is usually considered more secure than connecting using username and password. 
