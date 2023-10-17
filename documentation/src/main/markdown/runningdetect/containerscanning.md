# Container Scan

Container Scan is a way of running [solution_name] against any type of container image (including any non-Linux, non-Docker image) and providing component risk details for each layer of the image.

[solution_name] will accept either a user provided local file path, or remote HTTP/HTTPS URL to fetch a container image for scanning.

Container scan supports both persistent (Intelligent) and Stateless scan modes in [blackduck_product_name], but must be run independently of other scan types.

Execute Container Scan by adding the following to a run of [solution_name]:
````
--detect.tools=CONTAINER_SCAN
--detect.container.scan.file.path=<Path to local or HTTP/HTTPS URL for remote image>
````

## Requirements and Limitations

### General Requirements
 * Your [blackduck_product_name] server must have [blackduck_product_name] Secure Container (BDSC) licensed and enabled.
 * A unique project version must be provided, or the scan service will respond with an error.
 * Must be running [blackduck_product_name] 2023.10.0 or greater.
 * URL provided for a remote container image must use the HTTP(S) protocol.
 
## Invocation
 * To invoke a container scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum:   
 ```
--detect.tools=CONTAINER_SCAN
--detect.container.scan.file.path=<Path to local or URL for remote container>
```
	
* To invoke a stateless container scan the following must be provided at a minimum:   
```
--detect.tools=CONTAINER_SCAN
--detect.container.scan.file.path=<Path to local or URL for remote container>
--detect.blackduck.scan.mode=STATELESS
```
<note type="note">[solution_name] also supports Software Composition Analysis as a Service (SCAaaS), container scans that do not report on a layer by layer basis, however this is configured by Synopsys.</note>
## Results

Container scan findings will appear in the [blackduck_product_name] user interface unless the scan is executed in Stateless mode, please consult the documentation provided by [blackduck_product_name].
<!-- TBD Reference link directly to [blackduck_product_name] Docs once they are available
<xref href="ContainerScans.dita" scope="peer">Container scans
<data name="facets" value="pubname=bd-hub"/>
-->
<figure>
    <img src="images/containerscan.png"
         alt="Container Scan Results">
    <figcaption>Container Scan results in Black Duck displaying image layer findings.</figcaption>
</figure>

## Stateless mode results

In Stateless mode, Container Scan results are saved to a json file named `name_version_BlackDuck_DeveloperMode_Result.json` in the Scan Output directory, where `name` and `version` are the project's name and version.
