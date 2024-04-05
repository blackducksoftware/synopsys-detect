# [threat_intel] Scan

[threat_intel] Scans are a way of running binary file analysis that provides malware warnings, with a risk analysis level applied, for open source and commercial software.

[company_name] [solution_name] will accept a user provided local file path to a binary file for [threat_intel] Scan.

Identification of malware will be displayed to [blackduck_product_name] users will include file name, product (component name), and other identifiers, along with a description of the type of malware, if malware is detected.

## Workflow

 For [threat_intel] scans, [company_name] [solution_name] sends a BDIO header to scan service to generate a scan ID. Then the file for scanning is uploaded to [blackduck_product_name] Storage Service. Once uploaded, [threat_intel] service container receives a RabbitMQ message, indicating that there is a new scan request, at which point [threat_intel] Service takes the file from Storage Service and downloads it to its own container. The [threat_intel] Service calls the remote [threat_intel] API to analyze this file and sends a hash of the file to scan. Once complete, a report in JSON format is sent back to [threat_intel] Service, which is then forwarded to [blackduck_product_name] Scan Service. This report is saved in the [blackduck_product_name] database. 
<note type="note">The scanned file is removed from Storage Service when the scan completes, and [threat_intel] Service does not persist any data for this file.</note>

## Requirements and Limitations

### General Requirements
 * [blackduck_product_name] server must have the appropriate [threat_intel] license.
 * Must be running [blackduck_product_name] 2024.4.0 or greater.
 * [threat_intel] scans require network connectivity (Air gap mode is not supported).
 * [threat_intel] scan does not provide project and version name defaults so you need to set project and version names via properties when [threat_intel] is the only tool invoked. (If the specified project or version does not exist in [blackduck_product_name], it will be created.)
 
### Limitations
 * [threat_intel] Scan is limited to images of 5GB or less for hosted services.
 * [threat_intel] Scan is limited to images of 6GB or less for local, on-prem services.
 
## Invocation
To invoke a [threat_intel] scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum in addition to [blackduck_product_name] Server related configuration properties:   
 ```
--detect.tools=THREAT_INTEL
--detect.container.scan.file.path=<Path to local binary file>
--detect.project.name=<Use existing or set as a value to be created>
--detect.project.version.name=<Use existing or set as a value to be created>
```
 
## Results

[threat_intel] scan findings will appear in the [blackduck_product_name] user interface under the **Malware** tab. Further information on viewing results is available [here](https://sig-product-docs.synopsys.com/bundle/bd-hub/page/ComponentDiscovery/aboutReversinglabsScanning.html)

## Further information
For additional information regarding the related properties, see [threat-intel](../properties/configuration/threat-intel.md)
