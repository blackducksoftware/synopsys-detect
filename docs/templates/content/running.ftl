# Running ${solution_name}

## Positioning ${solution_name} in the build process

In most cases, ${solution_name} must be executed as a post-build step in the build environment of the project.
Building your project prior to running Synopsys Detect is often required for the Detector to run successfully,
and helps ensure that the build artifacts are available for signature scanning.

There are two ways to run ${solution_name}:

1. Download and run a ${solution_name} script
1. Download and run a ${solution_name} .jar file

The primary reason to run a ${solution_name} script is that the scripts (by default) always download
and run the latest version of ${solution_name}.

The primary reason to run the ${solution_name} .jar directly is direct control over the ${solution_name} version;
${solution_name} will never auto-update in this scenario.

## Running the ${solution_name} script

The primary function of the ${solution_name} scripts is to download and execute the ${solution_name} .jar file.
Several aspects of script functionality can be configured, including:

* The ${solution_name} version to download/run
* The download location
* Where to find Java
* Etc.

Information on how to configure the scripts can be found in [Shell script configuration](advanced/script-configuration.md).

### Linux or Mac (Bash)

To download and run the latest version of ${solution_name} in a single command:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

### Windows (PowerShell)

To download and run the latest version of ${solution_name} in a single command:

    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

### Running a specific version of ${solution_name}

#### Linux or Mac (Bash)

To run a specific version of ${solution_name}:

    export DETECT_LATEST_RELEASE_VERSION={${solution_name} version}
    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

For example, to run ${solution_name} 5.5.0:

    export DETECT_LATEST_RELEASE_VERSION={${solution_name} version}
    bash <(curl -s -L https://detect.synopsys.com/detect.sh)

#### Windows (PowerShell)

To run a specific version of ${solution_name}:

    $Env:DETECT_LATEST_RELEASE_VERSION = {${solution_name} version}
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

For example, to run ${solution_name} 5.5.0:

    $Env:DETECT_LATEST_RELEASE_VERSION = 5.5.0
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

## Running the ${solution_name} .jar

TBD

