# Running [solution_name] from within a Docker container

[solution_name] publishes Docker images which can be used to run [solution_name] from within a Docker container.

## To Use

To run a container built from a [solution_name] image, use the Docker CLI's `docker run` command.

* Use the -it options to view logs during the container run.

* Use the -v option to create a bind mount that will link a provided path to project source on your host to the /source directory within the container. Do this in place of providing the --detect.source.path property, as you would when running [solution_name] via the script or jar.

* You may also use the -v option to create a bind mount that will link a provided path to an output directory on your host to the /output directory within the container.  Do this in place of providing the --detect.output.path property, as you would when running [solution_name] via the script or jar.

* Use the --rm option to clean up the container once it exits.

* Provide [solution_name] property values as you would when running via the [solution_name] script or the [solution_name] jar, at the end of the `docker run` command.

Find available images [here](https://hub.docker.com/repository/docker/blackducksoftware/detect).

Find the source for them (Dockerfiles) [here](https://github.com/blackducksoftware/synopsys-detect-docker).

The format of image names is: `blackducksoftware/detect:[detect_version]-[package_manager]-[package_manager_version]`

* If you want an image with the latest supported release for a major version of [solution_name], and the latest supported version of a package manager, such images are named in the following format: `blackducksoftware/detect:[detect_major_version]-[package_manager]`

### [solution_name] Basic Images

If you wish to build your own custom [solution_name] image, to run [solution_name] in buildless mode, or to run non-detector tools such as the Signature Scanner or Binary Scanner, there also exist "simple" [solution_name] images.  These images contain no package manager files or executables.

The format of "simple" image names is: `blackducksoftware/detect:[detect_version]`

* If you want an image with the latest supported release for a major version of [solution_name], such images are named in the following format: `blackducksoftware/detect:[detect_major_version]`

#### [solution_name] Buildless Images

There also exist "buildless" [solution_name] images.  These images automatically pass the argument --detect.accuracy.required=NONE when running to make [solution_name] as resilient as possible (it will evaluate all applicable detectors, regardless of their accuracy, in order to get results).

The format of "buildless" image names is: `blackducksoftware/detect:[detect_version]-buildless`

* If you want a buildless image with the latest supported release for a major version of [solution_name], such images are named in the following format: `blackducksoftware/detect:[detect_major_version]-buildless`

#### [solution_name] IaC Images

If you wish to perform an IaC Scan via [solution_name] in a Docker container, there exist "iac" [solution_name] images.  The scanner that [solution_name] uses to perform IaC scans is not supported in other [solution_name] images.

The format of "iac" image names is: `blackducksoftware/detect:[detect_version]-iac`

* If you want an iac image with the latest supported release for a major version of [solution_name], such images are named in the following format: `blackducksoftware/detect:[detect_major_version]-iac`

### Examples

`docker run -it --rm -v [/path/to/source]:/source -v [/path/to/outputDir]:/output blackducksoftware/detect:[detect_image_tag] [detect_arguments]`

`docker run -it --rm -v /Home/my/gradle/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:7.0.0 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn`

`docker run -it --rm -v /Home/my/maven/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:7 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn`

`docker run -it --rm -v /Home/my/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:7.0.0 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn --detect.detector.buildless=true`

`docker run -it --rm -v /Home/my/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:6.9.1 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn --detect.tools=SIGNATURE_SCAN,BINARY_SCAN`
