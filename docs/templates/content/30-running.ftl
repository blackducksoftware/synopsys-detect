# Downloading and running

## Positioning ${solution_name} in the build process

### Build mode

In [build mode](../components/detectors/#build-detectors-versus-buildless-detectors), which is the default,
${solution_name} should be executed as a post-build step in the build environment of the project.
Building your project prior to running ${solution_name} is often required for the detector to run successfully,
and helps ensure that the build artifacts are available for signature scanning.

### Buildless mode

In [buildless mode](../components/detectors/#build-detectors-versus-buildless-detectors),
${solution_name} makes its best effort to discover dependencies without the benefit of
build artifacts or build tools. In buildless mode, there is no requirement that ${solution_name} must run as a post-build step.
Results from buildless mode may be less accurate than results from build mode.

## Choosing the working directory

You can run ${solution_name} from any directory. If you are not running ${solution_name} from the project directory,
provide the project directory using the [source path property](../properties/configuration/paths/#source-path). When that property is not set,
${solution_name} assumes the current working directory is the project directory.

## Choosing a run method (script, .jar, or Docker container)

There are three ways to run ${solution_name}:

1. Download and run a ${solution_name} script.
1. Download and run a ${solution_name} .jar file.
1. Run ${solution_name} from within a Docker container.

The primary reason to run one of the ${solution_name} scripts is that the scripts have an auto-update feature.
By default, they always
run the latest version of the ${solution_name} .jar file within a specific major version; downloading it for you if necessary.
When you run ${solution_name} via one of the provided scripts, you automatically pick up fixes and new features as they are released.
Each script limits itself to a specific ${solution_name} major version (for example, 7.y.z, or 6.y.z), unless you override
this default behavior.

| ${solution_name} version | Script Type | Script Name |
| --- | --- | --- |
| 7 | Bash | detect7.sh |
| 7 | PowerShell | detect7.ps1 |
| 6 | Bash | detect.sh |
| 6 | PowerShell | detect.ps1 |

Instuctions and examples in this documentation that reference the scripts assume you are running
${solution_name} 7, so refer to detect7.sh or detect7.ps1. To run ${solution_name} 6 instead,
simply substitute detect.sh for detect7.sh, or detect.ps1 for detect7.ps1.

The primary reason to run the ${solution_name} .jar directly is that this method provides
direct control over the exact ${solution_name} version;
${solution_name} does not automatically update in this scenario.

The primary reason to run ${solution_name} from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration;
${solution_name} does not automatically update in this scenario.

## Running the ${solution_name} script

The primary function of the ${solution_name} scripts is to download and execute the ${solution_name} .jar file.
Several aspects of script functionality can be configured, including:

* The ${solution_name} version to download/run; by default, the latest version.
* The download location.
* Where to find Java.

Information on how to configure the scripts is in [Shell script configuration](../advanced/script-configuration/).

#### Linux or Mac

On Linux or Mac, execute the ${solution_name} script (${bash_script_name}, which is a Bash script) from Bash.

To download and run the latest version of ${solution_name} in a single command:

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

Append any command line arguments to the end, separated by spaces. For example:

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

See [Quoting and escaping shell script arguments](../advanced/script-escaping-special-characters/) for details about quoting and escaping arguments.

#### Windows

On Windows, execute the ${solution_name} script (${powershell_script_name}, which is a PowerShell script) from
the [Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe).

To download and run the latest version of ${solution_name} in a single command:

````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

Append any command line arguments to the end, separated by spaces. For example:

````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect" --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

See [Quoting and escaping shell script arguments](../advanced/script-escaping-special-characters/) for details about quoting and escaping arguments.

### Running a specific version of ${solution_name}

#### Linux or Mac (Bash)

To run a specific version of ${solution_name}:

````
export DETECT_LATEST_RELEASE_VERSION={${solution_name} version}
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

For example, to run ${solution_name} version 5.5.0:

````
export DETECT_LATEST_RELEASE_VERSION=5.5.0
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

#### Windows (Command Prompt)

To run a specific version of ${solution_name}:

````
set DETECT_LATEST_RELEASE_VERSION={${solution_name} version}
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

For example, to run ${solution_name} version 5.5.0:

````
set DETECT_LATEST_RELEASE_VERSION=5.5.0
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

## Running the ${solution_name} .jar

Recent versions of the ${solution_name} .jar file are available for download from ${binary_repo_url_base}/${binary_repo_repo}/com/synopsys/integration/${project_name}.

To run ${solution_name} by invoking the .jar file:

````
java -jar {path to .jar file}
````

For example:

````
curl -O ${binary_repo_url_base}/${binary_repo_repo}/com/synopsys/integration/${project_name}/5.6.2/synopsys-detect-5.6.2.jar
java -jar synopsys-detect-5.6.2.jar
````

You can use the ${solution_name} Bash script (${bash_script_name}) to download the ${solution_name} .jar file:

````
export DETECT_DOWNLOAD_ONLY=1
./${bash_script_name}
````

## Running ${solution_name} from within a Docker container

${solution_name} publishes Docker images which can be used to run Detect from within a Docker container.

### To Use

To run a container built from a ${solution_name} image, use the Docker CLI's `docker run` command.

* Use the -it options to view logs during the container run.

* Use the -v option to create a bind mount that will link a provided path to project source on your host to the /source directory within the container. Do this in place of providing the --detect.source.path property, as you would when running ${solution_name} via the script or jar.

* You may also use the -v option to create a bind mount that will link a provided path to an output directory on your host to the /output directory within the container.  Do this in place of providing the --detect.output.path property, as you would when running ${solution_name} via the script or jar.

* Use the --rm option to clean up the container once it exits.

* Provide ${solution_name} property values as you would when running via the ${solution_name} script or the ${solution_name} jar, at the end of the `docker run` command.

Find available images [here](https://hub.docker.com/repository/docker/blackducksoftware/detect).

The format of image names is: blackducksoftware/detect:detect-[detect_version]-[package_manager]-[package_manager_version]

#### ${solution_name} Base Image

All Detect images are built from a base ${solution_name} image that can be used to build your own custom ${solution_name} image, to run ${solution_name} in buildless mode, or to run non-detector tools such as the Signature Scanner or Binary Scanner.

The format of base image names is: blackducksoftware/detect:detect-[detect_version]

#### Examples

`docker run -it --rm -v [/path/to/source]:/source -v [/path/to/outputDir]:/output blackducksoftware/detect:[detect_image_tag] [detect_arguments]`

`docker run -it --rm -v /Home/my/gradle/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:7.0.0-gradle-6.8.2 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn`

`docker run -it --rm -v /Home/my/maven/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:6.9.1-maven-3.8.1 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn`

`docker run -it --rm -v /Home/my/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:7.0.0 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn --detect.detector.buildless=true`

`docker run -it --rm -v /Home/my/project:/source -v /Home/where/I/want/detect/output/files:/output blackducksoftware/detect:6.9.1 --blackduck.url=https://my.blackduck.url --blackduck.api.token=MyT0kEn --detect.tools=SIGNATURE_SCAN,BINARY_SCAN`

## Choosing the target type

${solution_name} will select a workflow based in part on the target type you select via the *detect.target.type* property.

When running ${solution_name} on project source code, you'll probably want to
set *detect.target.type* to *SOURCE*, or leave *detect.target.type* unset (since
*SOURCE* is the default value).

When running ${solution_name} on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

### Common workflows

By default (detect.target.type=SOURCE), ${solution_name} will run the following on the source directory:

1. Any applicable detectors
1. ${blackduck_signature_scanner_name}

When a Docker image is provided and property *detect.target.type* is set to IMAGE, ${solution_name} will run the following on the image:

1. Docker Inspector
1. ${blackduck_signature_scanner_name}
1. ${blackduck_binary_scan_capability}

## Including and excluding tools and detectors

[Properties](../properties/all-properties/) provide a variety of additional options for configuring ${solution_name} behavior. One of the
most fundamental ways to modify ${solution_name} is by including and excluding [tools](../components/tools/) and [detectors](../components/detectors/).

### Tools

By default, all tools are eligible to run; the set of tools that actually run
depends on the properties you set.
To limit the eligible tools to a given list, use:

--detect.tools={comma-separated list of tool names, all uppercase}

To exclude specific tools, use:

````
--detect.tools.excluded={comma-separated list of tool names, all uppercase}
````

Exclusions take precedence over inclusions.

Refer to [Tools](../components/tools/) for the list of tool names.

Refer to [Properties](../properties/all-properties/) for details.

### Detectors

By default, all detectors are eligible to run.  The set of detectors that actually
run depends on the files existing in your project directory.
To limit the eligible detectors to a given list, use:

````
--detect.included.detector.types={comma-separated list of detector names}
````

To exclude specific detectors, use:

````
--detect.excluded.detector.types={comma-separated list of detector names}
````

Exclusions take precedence over inclusions.

Refer to [Detectors](../components/detectors/) for the list of detector names.

Refer to [Properties](../properties/all-properties/) for details.

## Running with ${blackduck_product_name}

${solution_name} can be used with ${blackduck_product_name} to perform Software Composition Analysis (SCA).

### Overview

When ${blackduck_product_name} connection details are provided, ${solution_name} executes
the following by default:

* The [detector tool](../components/detectors/), which runs the appropriate package manager-specific detector; the Maven detector
for Maven projects, the Gradle detector for Gradle projects, and so forth.
* The [${blackduck_signature_scanner_name}](../properties/configuration/signature scanner/), which performs a ${blackduck_signature_scan_act} on the
project directory.

${solution_name} can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [${blackduck_signature_scanner_name}](../properties/configuration/signature scanner/).
* Enable the [${impact_analysis_name}](../properties/configuration/impact analysis/#vulnerability-impact-analysis-enabled) on any Java project.
* Run [${blackduck_binary_scan_capability}](../properties/configuration/signature scanner/#binary-scan-target) on a given binary files.
* Run the ${dockerinspector_name} on a given [Docker image](../advanced/package-managers/docker-images/).
* Generate a [report](../properties/configuration/report/).
* Fail on [policy violation](../properties/configuration/project/#fail-on-policy-violation-severities-advanced).

Refer to [${blackduck_product_name} Server properties](../properties/configuration/blackduck server/)
and [${blackduck_signature_scanner_name} properties](../properties/configuration/signature scanner/) for details.

### Offline mode

If you do not have a ${blackduck_product_name} instance, or if your network is down, you can still run ${solution_name} in offline mode.
In offline mode, ${solution_name} creates the BDIO content and the dry run ${blackduck_signature_scan_act} output files without attempting to upload them to ${blackduck_product_name}.
You can run ${solution_name} in offline mode using the [offline mode property](../properties/configuration/blackduck server/#offline-mode).

### BDIO format

${solution_name} produces dependency information for Black Duck in Black Duck Input Output (BDIO) format files.
${solution_name} can produce BDIO files in two formats: BDIO version 1, or BDIO version 2.
Versions of Black Duck prior to 2018.12.4 accept only BDIO 1. Black Duck versions 2018.12.4 and higher
accept either BDIO 1 or BDIO 2.
By default, ${solution_name} produces BDIO 2 files.

Use the [BDIO2 enabled property](../properties/configuration/paths/#bdio-2-enabled) to select BDIO 1 format
(by disabling BDIO 2 format).

## Running with ${polaris_product_name}

This capability was deprecated in ${solution_name} 6, and disabled in ${solution_name} 7. All references
to it will be removed in ${solution_name} 8.

${solution_name} can be used with ${polaris_product_name} to perform Static Application Security Testing (SAST).

When ${polaris_product_name} connection details are provided, ${solution_name} executes
the following by default:

* The detector tool, which runs the appropriate package manager-specific detector; the Maven detector
for Maven projects, the Gradle detector for Gradle projects, and so forth.
* The ${polaris_product_name} tool, which runs the ${polaris_product_name} CLI on the
project directory.

Refer to [Properties](../properties/configuration/polaris/) for details.
