# Downloading and Running Detect

## Using Detect

- Deciding how to use Detect
- Choosing the working directory
- Positioning Synopsys Detect in the build process
    - Build mode
    - Buildless mode
- Choosing a run method
    - Running the Synopsys Detect script
    - Running a specific version of Synopsys Detect
    - Running the Synopsys Detect JAR file
- Running Synopsys Detect from within a Docker container
    - Synopsys Detect Basic Images
    - Synopsys Detect Buildless Images
    - Examples
- Choosing the target type
    - Common workflows
- Running with Black Duck
    - Connected to Black Duck
    - Offline mode
    - BDIO format
- Using tools and detectors

This page describes downloading and running Synopsys Detect.

## Deciding how to use Detect

Before you download and run Detect, you need to make the following decisions:

- In which directory do you want to run Detect?
- Do you want to run Detect before you build or after?
- Do you want to run Detect as a script or a .jar file; this affects which version is run.
- What [tools and detectors](../components) do you want to include or exclude?
- Do you want to run Detect offline, or connected to Black Duck?

## Choosing the working directory

You can run Synopsys Detect from any directory. If you are not running Synopsys Detect from the project directory,
provide the project directory using the [source path property](..//properties/configuration/paths/#source-path).
When that property is not set, Synopsys Detect assumes the current working directory is the project directory.

## Positioning Synopsys Detect in the build process

Choose a build mode or a buildless mode.

### Build mode

In build mode, which is the default, Synopsys Detect should be executed as a post-build step in the build environment of the project. Building your project prior to running Synopsys Detect is often required for the detector to run successfully, and helps ensure that the build artifacts are available for signature scanning.

### Buildless mode

In buildless mode, Synopsys Detect makes its best effort to discover dependencies without the benefit of build artifacts or build tools. In buildless mode, there is no requirement that Synopsys Detect must run as a post-build step. Results from buildless mode may be less accurate than results from build mode.

## Choosing a run method

There are three ways to run Synopsys Detect:

- Running the Synopsys Detect script.
- Running the Synopsys Detect .jar file.
- Run Synopsys Detect within a Docker container.

The primary reason to run one of the Synopsys Detect scripts is that the scripts have an auto-update feature. By default, they always run the latest version of the Synopsys Detect .jar file within a specific major version; downloading it for you if necessary.

When you run Synopsys Detect via one of the provided scripts, you automatically pick up fixes and new features as they are released. Each script limits itself to a specific Synopsys Detect major version (for example, 7.y.z, or 6.y.z), unless you override this default behavior.

| ${solution_name} version | Script type | Script name |
| ------------------------ | ----------- | ----------- |
| 7                        | Bash        | detect7.sh  |
| 7                        | PowerShell  | detect7.ps1 |
| 6                        | Bash        | detect.sh   |
| 6                        | PowerShell  | detect.ps1  |

Instructions and examples in this documentation that reference the scripts assume you are running Synopsys Detect 7, so refer to *detect7.sh* or *detect7.ps1*.

To run Synopsys Detect 6 instead, simply substitute *detect.sh* for *detect7.sh*, or *detect.ps1* for *detect7.ps1*.

The primary reason to run the Synopsys Detect .jar directly is that this method provides direct control over the exact Synopsys Detect version; Synopsys Detect does not automatically update in this scenario.

The primary reason to run Synopsys Detect from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration; Synopsys Detect does not automatically update in this scenario.

#### Running the Synopsys Detect script

The primary function of the Synopsys Detect scripts is to download and execute the Synopsys Detect .jar file.
Several aspects of script functionality can be configured, including:

- The Synopsys Detect version to download/run; by default, the latest version.
- The download location.
- Where to find Java

Information about how to configure the scripts is in the Shell script configuration.

##### Linux or Mac

On Linux or Mac, execute the Synopsys Detect script (detect7.sh, which is a Bash script) from Bash.

Use the following command to download and run the latest version of Synopsys Detect:
````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

Add command-line arguments, and separate using spaces. For example:
````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

##### Windows

On Windows, execute the Synopsys Detect script (detect7.ps1, which is a PowerShell script) from the [Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe).

Use the following command to download and run the latest version of Synopsys Detect:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

Add command-line arguments, and separate using spaces. For example:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect" --blackduck.url=https://blackduck.mydomain.com --blackduck.api.token=myaccesstoken
````

#### Running a specific version of Synopsys Detect

##### Linux or Mac (Bash)

To run a specific version of Synopsys Detect:
````
export DETECT_LATEST_RELEASE_VERSION={Synopsys Detect version}
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

For example, to run Synopsys Detect version 6.9.1:
````
export DETECT_LATEST_RELEASE_VERSION=6.9.1
bash <(curl -s -L https://detect.synopsys.com/detect7.sh)
````

##### Windows (Command prompt)

To run a specific version of Synopsys Detect:
````
set DETECT_LATEST_RELEASE_VERSION={Synopsys Detect version}
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

For example, to run Synopsys Detect version 6.9.1:
````
set DETECT_LATEST_RELEASE_VERSION=6.9.1
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

> View [${solution_name} properties](../properties/all-properties.md).

#### Running the Synopsys Detect JAR file

Download recent versions of the Synopsys Detect .jar file from
[https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect](https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect).

To run Synopsys Detect by invoking the .jar file:
````
java -jar {path to .jar file}
````
