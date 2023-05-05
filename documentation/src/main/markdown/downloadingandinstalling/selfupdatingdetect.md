# Self-updating [solution_name]

Self-updating [solution_name] allows the version management of [solution_name] across numerous pipelines being executed against the same [blackduck_product_name] instance.

## How self-updating [solution_name] functions
 
If the [solution_name] version has been hardcoded via [solution_name] version property it will continue download the requested version of [solution_name] if not already downloaded.

If the [solution_name] version has not been hardcoded via [solution_name] version property it will call the [blackduck_product_name] `api/tools/detect` endpoint for centralized [solution_name] version management to determine which version to download, as needed, and execute.

[solution_name] will download the required version from sig-repo or from a custom URL as configured in Black Duck.

<note type="restriction">Downgrading [solution_name] versions earlier than 8.9.0 is not supported.</note>
