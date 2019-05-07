## Overview ##
Synopsys Detect consolidates the functionality of Black Duck™ , Black Duck Binary Analysis™ (formerly known as Protecode SC) and Coverity™ on Polaris™ into a single solution. Synopsys Detect is designed to integrate natively into the build/CI environment and support all Coverity languages for Static Analysis. For Black Duck & Black Duck Binary Analysis, it makes it easier to set up and scan code bases using a variety of languages and package managers to identify open source risk.

## Build ##

[![Build Status](https://travis-ci.org/blackducksoftware/hub-gradle-plugin.svg?branch=master)](https://travis-ci.org/blackducksoftware/synopsys-detect)
[![Coverage Status](https://coveralls.io/repos/github/blackducksoftware/synopsys-detect/badge.svg?branch=master)](https://coveralls.io/github/blackducksoftware/synopsys-detect?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/synopsys-detect/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/synopsys-detect/branches/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=detect%3Adetect-application&metric=alert_status)](https://sonarcloud.io/dashboard?id=detect%3Adetect-application)

## Where can I get the latest release? ##

*Available from GitHub for Linux by running:*  
bash <(curl -s -L https://detect.synopsys.com/detect.sh)

*Available from GitHub for Windows by running:*  
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

For scripts, please see [Detect Scripts](https://github.com/synopsys-sig/synopsys-detect-scripts)

For AirGap, please use our [Artifactory](https://repo.blackducksoftware.com/artifactory/webapp/#/artifacts/browse/tree/General/bds-integrations-release/com/synopsys/integration/synopsys-detect).

## Documentation

[Quick Start Guide](https://github.com/blackducksoftware/synopsys-detect/wiki/Quick-Start-Guide)

All other documentation is located on our public [Confluence](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/62423113/Synopsys+Detect)
