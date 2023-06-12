# [solution_name] Version Management

[solution_name] self updating feature will allow customers who choose to enable Centralized [solution_name] Version Management in [blackduck_product_name] to automate the update of [solution_name] across their pipelines.

## Self updating [solution_name] scenarios

The Self Update feature will call the `/api/tools/detect` API to check for the existence of a mapped [solution_name] version in [blackduck_product_name]. If a version that is eligible for upgrade or downgrade has been mapped, the API will redirect the request to download that version and the current execution of [solution_name] will invoke the downloaded version to execute the requested scan. 

[solution_name] will download the required version from sig-repo or from a custom URL as configured in [blackduck_product_name]. Centralized [solution_name] Version Management feature support in [blackduck_product_name] is available from [blackduck_product_name] version 2023.4.0 onwards.

## Scenarios where [solution_name] self update will not execute

If there exists no mapping in [blackduck_product_name], or if the current version of [solution_name] matches the mapped version in [blackduck_product_name], or any issue occurs during the execution of the Self Update feature, then [solution_name] will continue with the current version to execute the scan.

If the [solution_name] URL of the [solution_name] .jar file to download and run has been hardcoded via [solution_name] property `DETECT_SOURCE` environment variable or the [solution_name] version set by the `DETECT_LATEST_RELEASE_VERSION` or `DETECT_VERSION_KEY` variables, self update will not occur. These are optional System environment properties used by Detect upgrade scripts.

If the [blackduck_product_name] “Internally Hosted” option has been selected and a [solution_name] download location has not been provided, the feature will not be enabled.

For further [blackduck_product_name] configuration information, refer to the documentation provided under the topic:
<xref href="DetectLocation.dita" scope="peer"> Hosting location for Synopsys Detect.
<data name="facets" value="pubname=bd-hub"/>

## [solution_name] log examples for self update

Downgrade to prior version blocked:  

``` 
2023-05-05 12:20:57 EDT INFO  \[main] - Detect-Self-Updater:  Checking https://test1.synopsys‎ .com/api/tools/detect API for centrally managed Detect version to download to /Users/testuser/tmp.   

2023-05-05 12:21:03 EDT WARN  \[main] - Detect-Self-Updater:  The Detect version 8.7.0 mapped at Black Duck server is not eligible for downgrade as it lacks the self-update feature. The self-update feature is available from 8.9.0 onwards.
```

Update to version allowed (8.9.0+):   

```
2023-05-05 12:33:52 EDT INFO  \[main] - Detect-Self-Updater:  Checking https://test1.synopsys‎ .com/api/tools/detect API for centrally managed Detect version to download to /Users/testuser/tmp.  

2023-05-05 12:33:53 EDT WARN  \[main] - Detect-Self-Updater:  The Detect version 8.9.2 mapped at Black Duck server is eligible for downgrade from the current version of 8.10.0. The self-update feature is available from 8.9.0 onwards.

2023-05-05 12:33:53 EDT INFO  \[main] - Detect-Self-Updater:  Centrally managed version of Detect was downloaded successfully and is ready to be run: /Users/testuser/tmp/synopsys-detect-8.9.2.jar.
```

Current version of [solution_name] matches the mapped version or there is no mapped version in [blackduck_product_name]:   

```
2023-05-05 12:33:52 EDT INFO  \[main] - Detect-Self-Updater:  Checking https://test1.synopsys‎ .com/api/tools/detect API for centrally managed Detect version to download to /Users/testuser/tmp.  

2023-05-05 12:33:53 EDT INFO  \[main] - Detect-Self-Updater:  Present Detect installation is up to date - skipping download.
```

<note type="important">
<ul>
<li>
Downgrading to versions earlier than 8.9.0 is not supported. 
</li>
<li>  
This feature is not available in offline 'blackduck.offline.mode=true' or AirGap configurations or if the Black Duck URL has not been provided via the `blackduck.url` variable.
<li>
When running an "Internally Hosted" instance of Synopsys Detect and using custom scripts, checks should be made to prevent Detect from querying Black Duck for version management and re-downloading itself.
<li>
Self update makes it easy to switch to a new major Synopsys Detect version, so care should be taken to validate that automated scanning is not impacted.
</li>
</ul>
</note>

