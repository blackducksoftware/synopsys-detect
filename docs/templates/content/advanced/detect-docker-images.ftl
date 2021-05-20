# Run Detect in a Docker Container

Detect publishes several Docker images which can be used to run Detect from within a Docker container.

## To Use

docker run -it -v [/path/to/source]:/source blackducksoftware/detect:[detect_version]-[package_manager]-[package_manager_version] [detect_arguments]

Find available images at (insert link here).

### Examples

docker run -it -v /Home/my/gradle/project:/source blackducksoftware/detect:7.0.0-gradle-6.8.2 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn

docker run -it -v /Home/my/maven/project:/source blackducksoftware/detect:6.9.1-maven-3.8.1 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn

### Detect Base Image

All Detect images are built from a base Detect image that can be used to build your own custom Detect image.

