# IaC Scan

IaC ("Infrastructure as Code") Scan is a type of scanning supported by [solution_name].

To configure IaC Scan, see [IaC Scan properties](../properties/configuration/iac-scan.md).

## Known Issues

### Strange Output on Windows VM

Issue: When running [solution_name] Iac Scan on a Windows Virtual Machine, users may see in the logs for the run that the output from the IaC Scanner may look like this:

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
This is due to a charset encoding incompatibility between [solution_name] and Windows VMs.

### IaC Code Location has default version

Issue: The code location generated for an IaC scan has [solution_name]'s default version, but you know [solution_name] can determine your project's actual version.

Solution: Enable Detectors to run via the property detect.tools (eg. --detect.tools=DETECTOR,IAC_SCAN), and a Detector should determine your project's actual version/reflect that in the code location produced by the IaC Scan.