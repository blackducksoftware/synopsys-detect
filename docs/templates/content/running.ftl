# Running

## Positioning ${solution_name} in the build process

In most cases, ${solution_name} must be executed as a post-build step in the build environment of the project.
Building your project prior to running Synopsys Detect is often required for the Detector to run successfully,
and helps ensure that the build artifacts are available for signature scanning.

## Choosing the working directory

You can run ${solution_name} from any directory. If you are not running Detect from the project directory,
provide the project directory via the detect.source.path property. When that property is not set,
${solution_name} assumes the current working directory is the project directory.

## Choosing a run method (script or .jar)

There are two ways to run ${solution_name}:

1. Download and run a ${solution_name} script
1. Download and run a ${solution_name} .jar file

The primary reason to run a ${solution_name} script is that the scripts (by default) always
run the latest version of the ${solution_name} .jar file (downloading it for you if necessary).
Run this way, ${solution_name} automatically updates itself; as soon as a new version becomes
available, you will run the new version (unless you override this default behavior).

The primary reason to run the ${solution_name} .jar directly is that this method provides
direct control over the ${solution_name} version;
${solution_name} will not automatically update in this scenario.

## Running the ${solution_name} script

The primary function of the ${solution_name} scripts is to download and execute the ${solution_name} .jar file.
Several aspects of script functionality can be configured, including:

* The ${solution_name} version to download/run (by default: the latest version)
* The download location
* Where to find Java
* Etc.

Information on how to configure the scripts can be found in [Shell script configuration](advanced/script-configuration.md).

#### Linux or Mac (Bash)

To download and run the latest version of ${solution_name} in a single command:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

#### Windows (PowerShell)

To download and run the latest version of ${solution_name} in a single command:

    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

### Running a specific version of ${solution_name}

#### Linux or Mac (Bash)

To run a specific version of ${solution_name}:

    export DETECT_LATEST_RELEASE_VERSION={${solution_name} version}
    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

For example, to run ${solution_name} 5.5.0:

    export DETECT_LATEST_RELEASE_VERSION=5.5.0
    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

#### Windows (PowerShell)

To run a specific version of ${solution_name}:

    $Env:DETECT_LATEST_RELEASE_VERSION = {${solution_name} version}
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

For example, to run ${solution_name} 5.5.0:

    $Env:DETECT_LATEST_RELEASE_VERSION = 5.5.0
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

## Running the ${solution_name} .jar

Recent versions of the ${solution_name} .jar file can be downloaded from ${binary_repo_url_base}/${binary_repo_repo}/com/synopsys/integration/${project_name}.

To run ${solution_name} by invoking the .jar file:

    java -jar {path to .jar file}

For example:

    curl -O ${binary_repo_url_base}/${binary_repo_repo}/com/synopsys/integration/${project_name}/5.6.2/synopsys-detect-5.6.2.jar
    java -jar synopsys-detect-5.6.2.jar

You can use the ${solution_name} Bash script (${bash_script_name}) to download the ${solution_name} .jar file:

    export DETECT_DOWNLOAD_ONLY=1
    ./${bash_script_name}

## Including and excluding tools and detectors

[Properties](/properties/all-properties) provide a variety of options for configuring ${solution_name} behavior. One of the
most fundamental ways to modify ${solution_name} is by including and excluding [tools](/components/tools) and [detectors](/components/detectors).

### Tools

By default, all tools are eligible to run (the set of tools that actually run
depends on which properties you set).
To limit the eligible tools to a given list, use:

    --detect.tools={comma-separated list of tool names, all uppercase}

To exclude specific tools, use:

    --detect.tools.excluded={comma-separated list of tool names, all uppercase}

Exclusions take precedence over inclusions.

See [Tools](/components/tools) for the list of tool names.

See [Properties](/properties/all-properties) for details.

### Detectors

By default, all detectors are eligible to run (the set of detectors that actually
run depends on what files exist in your project directory).
To limit the eligible detectors to a given list, use:

    --detect.included.detector.types={comma-separated list of detector names}

To exclude specific detectors, use:

    --detect.excluded.detector.types={comma-separated list of detector names}

Exclusions take precedence over inclusions.

See [Detectors](/components/detectors) for the list of detector names.

See [Properties](/properties/all-properties) for details.

