# Threat Intel Scan

Threat Intel Scan is a way of running binary file scans that provide malware warnings and risk analysis of open source and commercial software.

[company_name] [solution_name] will accept a user provided local file path to a binary file.

Identification of malware and other threats will be displayed to [blackduck_product_name] users at the component level, and will include file name, product (component name), product version, and publisher if identified.

Threat Intel Scan supports persistent (Intelligent) scan modes in [blackduck_product_name].

## Requirements and Limitations

### General Requirements
 * Your [blackduck_product_name] server must have the appropriate [blackduck_product_name] license.
 * Must be running [blackduck_product_name] 2024.4.0 or greater.
 * Threat Intel Scans require network connectivity (Air gap mode is not supported).
 * Threat Intel does not provide project and version name defaults to [company_name] [solution_name], so you need to set project and version names via properties when Threat Intel is the only tool invoked.
 
### Limitations
 * Threat Intel Scan is limited to images of 5GB or less for hosted services.
 * Threat Intel Scan is limited to images of 6GB or less for local, on-prem services.
 
## Invocation
To invoke a threat intel scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum:   
 ```
--detect.tools=THREAT_INTEL
--detect.container.scan.file.path=<Path to local binary file>
```
 
## Results

Threat intel scan findings will appear in the [blackduck_product_name] user interface, please consult the documentation [here](https://sig-product-docs.synopsys.com/bundle/bd-hub/page/Welcome.html)

## Further information
For additional information regarding the related properties, see [threat-intel](../properties/configuration/threat-intel.md)