# Container Scanning

Container Scanning is a way of running [solution_name] against any type of container image (including any non-Linux, non-Docker image) and providing component risk details for each layer of the image.

[solution_name] will accept either a user provided local file path, or remote HTTP/HTTPS URL to fetch a container image for scanning.

Container scanning supports both persistent (Intelligent) and Stateless scan modes in [blackduck_product_name].

Execute Container Scanning by adding the following to a run of [solution_name]:
````
--detect.container.scan.file.path=<Path to local or HTTP/HTTPS URL for remote image>
````

## Requirements and Limitations

### General Requirements
 * A unique project version must be provided, or the scan service will respond with an error.
 * Must be running [blackduck_product_name] 2023.7.0 or greater.
 * URL provided for a remote container image must use the HTTP(S) protocol.
 
## Invocation
 * To invoke a container scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum:
    * --detect.container.scan.file.path=<Path to local or URL for remote container> 
	
* To invoke a stateless container scan the following must be provided at a minimum:
    * --detect.container.scan.file.path=<Path to local or URL for remote container> --detect.blackduck.scan.mode=STATELESS

## Results

Container scan findings will appear in the [blackduck_product_name] user interface, please consult the documentation provided by [blackduck_product_name] under the topic:

<!-- TBD Reference link directly to [blackduck_product_name] Docs once they are available
<xref href="ContainerScans.dita" scope="peer">Container scans
<data name="facets" value="pubname=bd-hub"/>
-->

