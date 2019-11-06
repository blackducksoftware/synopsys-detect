# Configuring

The primary means for configuring ${solution_name} is by setting [${solution_name} property values](properties/all-properties.md).

${solution_name} reads property values using
[Spring Boot's configuration mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config).

The most common ways to pass a property value to ${solution_name} are:

* Using a command line argument

    --blackduck.url=https://blackduck.yourdomain.com

* Using a property assignment in a configuration (.properties) file

    blackduck.url=https://blackduck.yourdomain.com

* Using an environment variable

    export BLACKDUCK_URL=https://blackduck.yourdomain.com

When setting a property value on the command line, prefix the property name with two hyphens ("--").

On Linux, when setting a property value using an environment variable, the environment variable name
is the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_").

On Windows, the environment variable name can either be the original property
name, or the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_").

The most common location for a configuration (.properties) file is in a file named application.properties
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
Connecting to another system (e.g. Black Duck or Polaris) using an access token (also called an API token)
is usually considered more secure than connecting using username and password. 
