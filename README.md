## Overview

Black Duck Detect is an intelligent scan client that analyzes code in your projects and associated folders to perform compositional analysis. Detect can be configured to send scan results to Black Duck SCA, which generates risk analysis when identifying open-source components, licenses, and security vulnerabilities.

## Build

<!--[![Build Status](https://travis-ci.org/blackducksoftware/hub-gradle-plugin.svg?branch=master)](https://travis-ci.org/blackducksoftware/synopsys-detect)-->
<!--[![Coverage Status](https://coveralls.io/repos/github/blackducksoftware/synopsys-detect/badge.svg?branch=master)](https://coveralls.io/github/blackducksoftware/synopsys-detect?branch=master)-->
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<!--[![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/synopsys-detect/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/synopsys-detect/branches/master)-->
<!--[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.synopsys.integration%3Asynopsys-detect&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.synopsys.integration%3Asynopsys-detect)-->

## Where can I get the latest release?

*Available for Linux/MacOS by running:*

```bash
bash <(curl -s -L https://detect.blackduck.com/detect10.sh)
```

*Available for Windows by running in **command prompt**:*

```cmd
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.blackduck.com/detect10.ps1?$(Get-Random) | iex; detect"
```

*Available for Windows/Linux by running in **powershell**:*
```powershell
[Net.ServicePointManager]::SecurityProtocol = 'tls12'; $Env:DETECT_EXIT_CODE_PASSTHRU=1; irm https://detect.blackduck.com/detect10.ps1?$(Get-Random) | iex; detect
```

For scripts, please see [Detect Scripts](https://detect.blackduck.com).

For AirGap, please use our [Repo](https://repo.blackduck.com/bds-integrations-release/com/blackduck/integration/detect/)

## Documentation

The latest quickstart documentation is [here](https://documentation.blackduck.com/bundle/detect/page/gettingstarted/quickstart.html).

The latest full documentation is [here](https://documentation.blackduck.com/bundle/detect/page/introduction.html).

## Getting help

Additional information and help is available from the
[Black Duck Community](https://community.blackduck.com/s/).
