# ReversingLabs Scan

ReversingLabs Scans are a way of running binary file analysis that provides malware warnings and risk analysis of open source and commercial software.

[company_name] [solution_name] will accept a user provided local file path to a binary file for ReversingLabs Scan.

Identification of malware and other threats will be displayed to [blackduck_product_name] users at the component level, and will include file name, product (component name), product version, and publisher if identified.

ReversingLabs Scan supports persistent (Intelligent) scan modes in [blackduck_product_name].

## Requirements and Limitations

### General Requirements
 * [blackduck_product_name] server must have the appropriate ReversingLabs license.
 * Must be running [blackduck_product_name] 2024.4.0 or greater.
 * ReversingLabs scans require network connectivity (Air gap mode is not supported).
 * ReversingLabs does not provide project and version name defaults to [company_name] [solution_name], so you need to set project and version names via properties when ReversingLabs is the only tool invoked.
 
### Limitations
 * ReversingLabs Scan is limited to images of 5GB or less for hosted services.
 * ReversingLabs Scan is limited to images of 6GB or less for local, on-prem services.
 
## Invocation
To invoke a ReversingLabs scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum in addition to [blackduck_product_name] Server related configuration properties:   
 ```
--detect.tools=THREAT_INTEL
--detect.container.scan.file.path=<Path to local binary file>
--detect.project.name=<Use existing or set as a value to be created>
--detect.project.version.name=<Use existing or set as a value to be created>
```
 
## Results

ReversingLabs scan findings will appear in the [blackduck_product_name] user interface, please consult the documentation [here](https://sig-product-docs.synopsys.com/bundle/bd-hub/page/Welcome.html)

## Further information
For additional information regarding the related properties, see [threat-intel](../properties/configuration/threat-intel.md)
