# IaC Scan

IaC ("Infrastructure as Code") Scan is a type of scanning supported by [company_name] [solution_name] that involves scanning code or configuration files used for automated management and provisioning of infrastructure (networks, virtual machines, load balancers, and connection topology).

To configure IaC Scan, see [IaC Scan properties](../properties/configuration/iac-scan.md).

See the [blackduck_product_name] documentation for futher details on <a href="https://sig-product-docs.synopsys.com/bundle/bd-hub/page/InternalProjectVersions/infrastructureAsCode.html" target="_blank">Infrastructure as Code scanning.</a>

## Known Issues

### Strange Output on Windows VM

Issue: When running [company_name] [solution_name] Iac Scan on a Windows Virtual Machine, users may see in the logs for the run that the output from the IaC Scanner may look like this:

````
 --- ←[4mIdentified←[0m
 ---
 --- ←[38;5;239mΓöé←[39mFile Type ←[38;5;239mΓöé←[39mOccurrences←[38;5;239mΓöé←[39m
````
as supposed to this:

````
 --- Identified
 --- 
 --- │File Type │Occurrences│

````
This is due to a charset encoding incompatibility between [company_name] [solution_name] and Windows VMs.

### IaC Code Location has default version

Issue: The code location generated for an IaC scan has [company_name] [solution_name]'s default version, but you know [company_name] [solution_name] can determine your project's actual version.

Solution: Enable Detectors to run via the property detect.tools (eg. --detect.tools=DETECTOR,IAC_SCAN), and a Detector should determine your project's actual version/reflect that in the code location produced by the IaC Scan.