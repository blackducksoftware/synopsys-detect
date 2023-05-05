# Self-updating [solution_name]

Self-updating [solution_name] allows for the version management of [solution_name] across numerous pipeline scans being executed against the same [blackduck_product_name] instance.

## How self-updating [solution_name] functions
 
If the [solution_name] version has been hardcoded via [solution_name] version property it will continue download the requested version of [solution_name] if not already downloaded.

If the [solution_name] version has not been hardcoded via [solution_name] version property it will call the [blackduck_product_name] `api/tools/detect` endpoint for centralized [solution_name] version management to determine which version to download, as needed, and execute.

[solution_name] will download the required version from sig-repo or from a custom URL as configured in Black Duck.   

For further [blackduck_product_name] configuration information, consult the documentation provided under the topic:
<xref href="DetectLocation.dita" scope="peer"> Hosting location for Synopsys Detect.
<data name="facets" value="pubname=bd-hub"/>

<note type="restrictions">
<ul>
<li>
Downgrading to versions earlier than 8.9.0 is not supported. 
</li>
<li>  
This feature is not available in offline or AirGap configurations.
</li>
</ul>
</note>


