# Additional configuration methods and details

[solution_name] reads property values using
[Spring Boot's externalized configuration mechanism](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/spring-boot-features.html#boot-features-external-config),
which provides capabilities beyond those described on this page.

The most common methods used to pass a property value to [solution_name] are listed as follows. A method with a lower number in Spring Boot's order of precedence overrides a method with a higher number.

* Using a command line argument, (#4 in Spring Boot's order of precedence):
````
--blackduck.url=https://blackduck.yourdomain.com
````
* Using one environment variable per property, (#10 in Spring Boot's order of precedence):
````
export BLACKDUCK_URL=https://blackduck.yourdomain.com
````
* Using property assignments in a .properties configuration file, (#14 in Spring Boot's order of precedence):
````
blackduck.url=https://blackduck.yourdomain.com
blackduck.api.token=youraccesstoken
````
* Using property assignments in a .yml configuration file, (#14 in Spring Boot's order of precedence, however .properties takes precedence over .yml):
````
blackduck.url: https://blackduck.yourdomain.com
blackduck.api.token: youraccesstoken
````
* Using the SPRING_APPLICATION_JSON environment variable with a set of properties set using JSON format, (#5 in Spring Boot's order of precedence):
````
export SPRING_APPLICATION_JSON='{"blackduck.url":"https://blackduck.yourdomain.com","blackduck.api.token":"youraccesstoken"}'
````
* Cross referencing a system property as a value in the Spring Boot JSON property, (#10 in Spring Boot's order of precedence):
````
export SPRING_APPLICATION_JSON='{"blackduck.url":"${BLACKDUCK_URL}"}'
````

Refer to the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/spring-boot-features.html#boot-features-external-config)
for more details and advanced ways to set properties.
