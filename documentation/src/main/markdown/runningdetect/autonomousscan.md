# Autonomous Scanning

Autonomous Scanning allows for [company_name] [solution_name] to run scans with a minimum amount of user provided parameters. Autonomous scans store configuration and related parameters in a .json file that can be shared between users working with the same code. This reduces the user input required to effectively analyze source code and binary files, along with simplifying repeat analysis and delta reporting.

Autonomous Scanning will accept a user provided local or remote file path to source of binary files. This file may be a single executable, a directory of source files, or a combination thereof.

The scan settings file name is a hash generated from the scanned folder(s). An initial scan with user provided parameters will populate this file and subsequent scans of the same folder structure will update it. After the initial scan, unless you wish to override previous scan parameters, [company_name] [solution_name] can be run in Autonomous mode by simply providing the `--detect.autonomous.scan.enabled=true` parameter.
<note type="note">The scan settings json file is generated and updated automatically by [company_name] [solution_name] and should not be manually modified.</note>

## Initial Scan Workflow

1. Run [company_name] [solution_name] in Autonomous mode by providing the `--detect.autonomous.scan.enabled=true` parameter and any other supported parameters that you require. (See limitations section for parameters that are not supported in Autonomous mode.)
1. [company_name] [solution_name] will determine which tools and detectors are appropriate and available to run, including Package Manager, Signature, and Binary Scanning.
1. Scans will include any analyzable content of user specified locations as well as source or binaries located in the run directory.
1. Once complete, scan findings can be viewed in the BDIO file produced, or in the [blackduck_product_name] UI if [blackduck_product_name] has been configured.

## Subsequent Scan Workflow

1. Run [company_name] [solution_name] by providing the `--detect.autonomous.scan.enabled=true` parameter.
1. [company_name] [solution_name] will determine, and run, the appropriate available tools and detectors.
1. Once complete, scan findings can be viewed in the BDIO file produced, or in the [blackduck_product_name] UI if [blackduck_product_name] has been configured.

## Requirements and Limitations

### General Requirements

 * Scans require network connectivity when used with [blackduck_product_name] or if the scan location is remote.
 
 ### Limitations
 * Autonomous scanning does not support flags.
 * The following settings will not be used or persisted by [company_name] [solution_name] when running in Autonomous mode:
    * --detect.tools
    * --detect.diagnostic
* The following setting will not be persisted by [company_name] [solution_name] when running in Autonomous mode:
    * --blackduck.api.token

## Invocation without [blackduck_product_name]
To invoke an Autonomous scan in Intelligent Mode, the following must be provided at a minimum:   
 ```
--detect.autonomous.scan.enabled=true
```
## Invocation with [blackduck_product_name]
To invoke an Autonomous scan in Rapid Mode with [blackduck_product_name], the following must be provided at a minimum:   
 ```
--detect.autonomous.scan.enabled=true
--blackduck.url=<https://my.blackduck.url>
--blackduck.api.token=<MyT0kEn>
```

## Results
Autonomous scan findings will be stored in the [BDIO](../properties/configuration/paths.html#ariaid-title4) file when run without [blackduck_product_name].

Autonomous scan findings will appear in the [blackduck_product_name] user interface if [blackduck_product_name] is configured.

## Debug Logging
Run [company_name] [solution_name] with `--logging.level.detect=DEBUG` to view the parameters being applied during Autonomous scans.