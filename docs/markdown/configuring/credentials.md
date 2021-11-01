# Providing sensitive values such as credentials

You can provide sensitive values such as credentials to [solution_name] using a variety of
mechanisms provided by [Spring Boot](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/spring-boot-features.html#boot-features-external-config),
including:

* On the command line; for example, --blackduck.api.token={your access token}.
* As an environment variable value; for example, export BLACKDUCK_API_TOKEN={your access token}.
* In a configuration (.properties) file; for example, ./application.properties.

Values provided on the command line may be visible to other users that can view process details.
Setting sensitive values using environment variables is usually considered more secure.
