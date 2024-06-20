# Autonomous Scanning

Autonomous Scanning allows for [company_name] [solution_name] to run scans with a minimum amount of user provided parameters. Autonomous scans store configuration and related parameters in a .json file that can be shared between users working with the same code. This reduces the user input required to effectively analyze source code and binary files, along with simplifying repeat analysis and delta reporting.

Autonomous Scanning will accept a user provided local file path or remote HTTP/HTTPS URL to fetch content for scanning. The content may be a single executable, a directory of source files, or a combination thereof.

The scan settings file name is a hash generated from the scanned folder(s). An initial scan with user provided parameters will populate this file and subsequent scans of the same folder structure will update it. After the initial scan, unless you wish to override previous scan parameters, [company_name] [solution_name] can be run in Autonomous mode by simply providing the `--detect.autonomous.scan.enabled=true` parameter.

[company_name] [solution_name] properties, environment variables, or Spring configurations enabled at run time will take precedence over values stored in the scan settings file.    

<note type="warning">The scan settings json file is generated and updated automatically by [company_name] [solution_name] and should not be manually modified.</note>

[company_name] [solution_name] will determine appropriate tools and/or Detectors to run given the content of the target path or run folder if no path is provided. The determining factor for scan types include the file type, and whether the pre-requisites of the appropriate Detector types are met. If prerequisites for package manager or binary scanning are not met, but files are available in the target folder, a signature scan will be run. [company_name] [solution_name] will follow the [Detector Cascade](/detectorcascade.md) processing order. 

## Initial Scan Workflow

1. Run [company_name] [solution_name] in Autonomous mode by providing the `--detect.autonomous.scan.enabled=true` parameter and any other supported parameters that you require. (See limitations section for parameters that are not supported in Autonomous mode.)
1. [company_name] [solution_name] will determine which tools and detectors are appropriate and available to run, including Package Manager, Signature, and Binary Scanning.
1. Scans will include any analyzable content of user specified locations as well as source or binaries located in the run directory.
1. Once complete, scan findings can be viewed in the BDIO file produced, or in the [blackduck_product_name] UI if [blackduck_product_name] has been configured.

## Subsequent Scan Workflow

1. Run [company_name] [solution_name] by providing the `--detect.autonomous.scan.enabled=true` parameter.
1. [company_name] [solution_name] will determine if any user provided arguments or properties should take precedence over values in the existing scan settings file, and run the appropriate available tools and detectors.
1. Once complete, scan findings can be viewed in the BDIO file produced, or in the [blackduck_product_name] UI if [blackduck_product_name] has been configured.
## Scan mode scenarios

- [ONLINE mode](../properties/configuration/blackduck-server.md): With [blackduck_product_name] configuration or proxy configuration set and `blackduck.offline.mode` set to false, or overridden as false in the scan settings file, the scan will run online.

	- [Rapid](rapidscan.md): Will run if the [scan path](../properties/configuration/paths.html#scaaas-scan-target) `--scaaas.scan.path` is provided and `--detect.tools=DETECTOR` is set, or `--detect.blackduck.scan.mode` is set to RAPID.

	- [Stateless](statelessscan.md)): Will run if `--scaaas.scan.path` is provided and `--detect.tools=(any value`) other than DETECTOR, or `--detect.blackduck.scan.mode` is set to STATELESS.

	- Intelligent scan: Runs when [scan mode](../properties/configuration/blackduck-server.html#ariaid-title7) is not set to RAPID or STATELESS, or `--detect.blackduck.scan.mode` is explicitly set to INTELLIGENT.

- [OFFLINE mode](../properties/configuration/blackduck-server.md#offline-mode): When `--blackduck.offline.mode` is true or there is no [blackduck_product_name] url or [blackduck_product_name] proxy information provided. (Warning messages will be logged for binary and signature scans.)

	- [Binary scan](https://sig-product-docs.synopsys.com/bundle/bd-hub/page/BinaryAnalysis/Overview.html): Will run if one or more binary files exist in the scan directory and [blackduck_product_name] configuration is completed.

	- Signature scan: Will run against source files if scan cli is available. If [scan cli](https://sig-product-docs.synopsys.com/bundle/bd-hub/page/ComponentDiscovery/DownloadAndInstall.html) is not locally available, it will need to be downloaded from [blackduck_product_name].

## Requirements and Limitations

### General Requirements

 * Scans require local network connectivity when used with [blackduck_product_name] or if the scan location is remote, remote network connectivity is required.
 * [blackduck_product_name] must be configured for binary scans.
 
 ### Limitations
 * Autonomous scanning does not support flags.
 * [blackduck_product_name] Snippet scans are not supported.
 * The following settings will not be used or persisted by [company_name] [solution_name] when running in Autonomous mode:
    * --detect.tools
    * --detect.diagnostic
* The following setting will not be persisted by [company_name] [solution_name] when running in Autonomous mode:
    * --blackduck.api.token

## Invocation without [blackduck_product_name]
To invoke an Autonomous scan without [blackduck_product_name] integration, the following must be provided at a minimum:   
 ```
--detect.autonomous.scan.enabled=true
```
## Invocation with [blackduck_product_name]
To invoke an Autonomous scan with [blackduck_product_name], the following must be provided at a minimum:   
 ```
--detect.autonomous.scan.enabled=true
--blackduck.url=<https://my.blackduck.url>
--blackduck.api.token=<MyT0kEn>
```

## Results
Autonomous scan findings will be stored in a [BDIO](../properties/configuration/paths.html#ariaid-title4) file when run without [blackduck_product_name].

Autonomous scan findings will appear in the [blackduck_product_name] user interface if [blackduck_product_name] is configured.

## Debug Logging
Run [company_name] [solution_name] with `--logging.level.detect=DEBUG` to view the parameters being applied during Autonomous scans.