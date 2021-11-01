# Downloading and Running [solution_name]

This page describes downloading and running [solution_name].

## Deciding how to use [solution_name]

Before you download and run [solution_name], you need to make the following decisions:

- Do you want to run [solution_name] before you build or after?
- In which directory do you want to run [solution_name]?
- Do you want to run [solution_name] as a script or a .jar file; this affects which version is run.
- What [tools and detectors](../components/overview.md) do you want to include or exclude?
- Do you want to run [solution_name] offline, or connected to [blackduck_product_name].

## Positioning [solution_name] in the build process

### Build mode

In [build mode](../components/detectors.md#build-detectors-versus-buildless-detectors), which is the default,
[solution_name] should be executed as a post-build step in the build environment of the project.
Building your project prior to running [solution_name] is often required for the detector to run successfully,
and helps ensure that the build artifacts are available for signature scanning.

### Buildless mode

In [buildless mode](../components/detectors.md#build-detectors-versus-buildless-detectors),
[solution_name] makes its best effort to discover dependencies without the benefit of
build artifacts or build tools. In buildless mode, there is no requirement that [solution_name] must run as a post-build step.
Results from buildless mode may be less accurate than results from build mode.

## Choosing the working directory

You can run [solution_name] from any directory. If you are not running [solution_name] from the project directory,
provide the project directory using the [source path property](../properties/configuration/paths.md#source-path). When that property is not set,
[solution_name] assumes the current working directory is the project directory.

## Choosing a run method (script, .jar, or Docker container)

There are three ways to run [solution_name]:

1. Download and run a [solution_name] script.
1. Download and run a [solution_name] .jar file.
1. Run [solution_name] from within a Docker container.

The primary reason to run one of the [solution_name] scripts is that the scripts have an auto-update feature.
By default, they always
run the latest version of the [solution_name] .jar file within a specific major version; downloading it for you if necessary.
When you run [solution_name] via one of the provided scripts, you automatically pick up fixes and new features as they are released.
Each script limits itself to a specific [solution_name] major version (for example, 7.y.z, or 6.y.z), unless you override
this default behavior.

| [solution_name] version | Script Type | Script Name |
| --- | --- | --- |
| 7 | Bash | detect7.sh |
| 7 | PowerShell | detect7.ps1 |
| 6 | Bash | detect.sh |
| 6 | PowerShell | detect.ps1 |

Instuctions and examples in this documentation that reference the scripts assume you are running
[solution_name] 7, so refer to detect7.sh or detect7.ps1. To run [solution_name] 6 instead,
simply substitute detect.sh for detect7.sh, or detect.ps1 for detect7.ps1.

The primary reason to run the [solution_name] .jar directly is that this method provides
direct control over the exact [solution_name] version;
[solution_name] does not automatically update in this scenario.

The primary reason to run [solution_name] from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration;
[solution_name] does not automatically update in this scenario.

## Running the [solution_name] script

The primary function of the [solution_name] scripts is to download and execute the [solution_name] .jar file.
Several aspects of script functionality can be configured, including:

* The [solution_name] version to download/run; by default, the latest version.
* The download location.
* Where to find Java.

Information on how to configure the scripts is in [Shell script configuration](../scripts/overview.md).

### Running the script on Linux or Mac

On Linux or Mac, execute the [solution_name] script ([bash_script_name], which is a Bash script) from Bash.

To download and run the latest version of [solution_name] in a single command:

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

Append any command line arguments to the end, separated by spaces. For example:

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

See [Quoting and escaping shell script arguments](../scripts/script-escaping-special-characters.md) for details about quoting and escaping arguments.

#### To run a specific version of [solution_name]:

````
export DETECT_LATEST_RELEASE_VERSION={[solution_name] version}
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

For example, to run [solution_name] version 5.5.0:

````
export DETECT_LATEST_RELEASE_VERSION=5.5.0
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

### Running the script on Windows

On Windows, execute the [solution_name] script ([powershell_script_name], which is a PowerShell script) from
the [Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe).

To download and run the latest version of [solution_name] in a single command:

````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

Append any command line arguments to the end, separated by spaces. For example:

````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect" --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

#### To run a specific version of [solution_name]:

````
set DETECT_LATEST_RELEASE_VERSION={[solution_name] version}
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

For example, to run [solution_name] version 5.5.0:

````
set DETECT_LATEST_RELEASE_VERSION=5.5.0
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

See [Quoting and escaping shell script arguments](../scripts/script-escaping-special-characters.md) for details about quoting and escaping arguments.

## Running the [solution_name] .jar

Recent versions of the [solution_name] .jar file are available for download from [binary_repo_url_base]/[binary_repo_repo]/com/synopsys/integration/[project_name].

To run [solution_name] by invoking the .jar file:

````
java -jar {path to .jar file}
````

For example:

````
curl -O [binary_repo_url_base]/[binary_repo_repo]/com/synopsys/integration/[project_name]/5.6.2/synopsys-detect-5.6.2.jar
java -jar synopsys-detect-5.6.2.jar
````

You can use the [solution_name] Bash script ([bash_script_name]) to download the [solution_name] .jar file:

````
export DETECT_DOWNLOAD_ONLY=1
./[bash_script_name]
````

## Choosing the target type

[solution_name] will select a workflow based in part on the target type you select via the *detect.target.type* property.

When running [solution_name] on project source code, you'll probably want to
set *detect.target.type* to *SOURCE*, or leave *detect.target.type* unset (since
*SOURCE* is the default value).

When running [solution_name] on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

### Common workflows

By default (detect.target.type=SOURCE), [solution_name] will run the following on the source directory:

1. Any applicable detectors
1. [blackduck_signature_scanner_name]

When a Docker image is provided and property *detect.target.type* is set to IMAGE, [solution_name] will run the following on the image:

1. Docker Inspector
1. [blackduck_signature_scanner_name]
1. [blackduck_binary_scan_capability]

## Running with [blackduck_product_name]

[solution_name] can be used with [blackduck_product_name] to perform Software Composition Analysis (SCA).

### Overview

When [blackduck_product_name] connection details are provided, [solution_name] executes
the following by default:

* The [detector tool](../components/detectors.md), which runs the appropriate package manager-specific detector; the Maven detector
for Maven projects, the Gradle detector for Gradle projects, and so forth.
* The [[blackduck_signature_scanner_name]](../properties/configuration/signature-scanner.md), which performs a [blackduck_signature_scan_act] on the
project directory.

[solution_name] can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [[blackduck_signature_scanner_name]](../properties/configuration/signature-scanner.md).
* Enable the [[impact_analysis_name]](../properties/configuration/impact-analysis.md#vulnerability-impact-analysis-enabled) on any Java project.
* Run [[blackduck_binary_scan_capability]](../properties/configuration/binary-scanner.md) on a given binary files.

* Run the [dockerinspector_name] on a given [Docker image](../packagemgrs/docker-images.md).
* Generate a [report](../properties/configuration/report.md).
* Fail on [policy violation](../properties/configuration/project.md#fail-on-policy-violation-severities-advanced).

Refer to [[blackduck_product_name] Server properties](../properties/configuration/blackduck-server.md)
and [[blackduck_signature_scanner_name] properties](../properties/configuration/signature-scanner.md) for details.

### Offline mode

If you do not have a [blackduck_product_name] instance, or if your network is down, you can still run [solution_name] in offline mode.
In offline mode, [solution_name] creates the BDIO content and the dry run [blackduck_signature_scan_act] output files without attempting to upload them to [blackduck_product_name].
You can run [solution_name] in offline mode using the [offline mode property](../properties/configuration/blackduck-server.md#offline-mode).

### BDIO format

[solution_name] produces dependency information for [blackduck_product_name] in Black Duck Input Output (BDIO) format files.
[solution_name] can produce BDIO files in two formats: BDIO version 1, or BDIO version 2.
Versions of [blackduck_product_name] prior to 2018.12.4 accept only BDIO 1.
[blackduck_product_name] versions 2018.12.4 and higher accept either BDIO 1 or BDIO 2.
By default, [solution_name] produces BDIO 2 files.

Use the [BDIO2 enabled property](../properties/configuration/paths.md#bdio-2-enabled-deprecated) to select BDIO 1 format
(by disabling BDIO 2 format).

