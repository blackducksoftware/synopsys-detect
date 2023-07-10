## Overview

Synopsys Detect scans code bases in your projects and folders to perform compositional analysis and functions as an intelligent scan client for Black Duck and other Synopsys SCA products. Synopsys Detect can send scan results to Black Duck, which generates risk analysis when identifying open source components, licenses, and security vulnerabilities.

## Build

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.synopsys.integration%3Asynopsys-detect&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.synopsys.integration%3Asynopsys-detect)

## Where can I get the latest release?

*Available from GitHub for Linux/MacOS by running:*

```bash
bash <(curl -s -L https://detect.synopsys.com/detect8.sh)
```

*Available from GitHub for Windows by running in **command prompt**:*

```cmd
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect"
```

*Available from GitHub for Windows/Linux by running in **powershell**:*
```powershell
[Net.ServicePointManager]::SecurityProtocol = 'tls12'; $Env:DETECT_EXIT_CODE_PASSTHRU=1; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect
```

For scripts, please see [Detect Scripts](https://github.com/synopsys-sig/synopsys-detect-scripts).

For AirGap, please use our [Artifactory](https://sig-repo.synopsys.com/webapp/#/artifacts/browse/tree/General/bds-integrations-release/com/synopsys/integration/synopsys-detect).

## Documentation

The latest quickstart documentation is [here](https://sig-product-docs.synopsys.com/bundle/integrations-detect/page/gettingstarted/quickstart.html).

The latest full documentation is [here](https://sig-product-docs.synopsys.com/bundle/integrations-detect/page/introduction.html).

Links to certain earlier versions can be found [here](https://community.synopsys.com/s/user-guide-archive).

## Getting help

Additional information and help is available from the
[Synopsys Software Integrity Community](https://community.synopsys.com/s/).
