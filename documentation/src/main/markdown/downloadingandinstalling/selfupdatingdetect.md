# [company_name] [solution_name] Version Management

[company_name] [solution_name] self-updating feature will allow customers who choose to enable Centralized [company_name] [solution_name] Version Management in [blackduck_product_name] to automate the update of [company_name] [solution_name] across their pipelines.

## Self updating [company_name] [solution_name] scenarios

The Self Update feature will call the `/api/tools/detect` API end point to check for the existence of a specified [company_name] [solution_name] version in [blackduck_product_name] under the **Admin > System Settings > [company_name] [solution_name] > [company_name] [solution_name] Version** drop-down. If a version that is eligible for upgrade or downgrade has been specified, the request to download that version of the [company_name] [solution_name] .jar will execute and the current run of [company_name] [solution_name] will invoke it for the requested scan. 

[company_name] [solution_name] will download the required version from Synopsys "sig-repo" repository when the service is Synopsys hosted, or from a custom URL as configured in [blackduck_product_name], when internally hosted. To support self-update via internal hosting, the [company_name] [solution_name] binary must be in a location accessible via https to all executing [company_name] [solution_name] instances.   
Centralized [company_name] [solution_name] Version Management feature support in [blackduck_product_name] is available from [blackduck_product_name] version 2023.4.0 onwards.

<!-- Variables do not resolve when in a note format hence the hardcoding below -->
<note type="information">
<ul>
<li>
If the Black Duck **Internally Hosted** option has been configured, Synopsys Detect will be downloaded via https on the client side from the fully formatted URL specified under the "Hosting Location for Synopsys Detect" setting. Note that this setting over-rides version and integrity checks that would otherwise be performed by Synopsys Detect.
<li>
If the Black Duck **Synopsys Hosted** option has been configued, Synopsys Detect will be downloaded on the client side from the Synopsys "sig-repo" repository.
</li>
</ul>
</note>

## Scenarios where [company_name] [solution_name] self update will not execute

If there exists no mapping in [blackduck_product_name], or if the current version of [company_name] [solution_name] matches the mapped version in [blackduck_product_name], or any issue occurs during the execution of the Self Update feature, then [company_name] [solution_name] will continue with the current version to execute the scan.

If the [company_name] [solution_name] URL of the [company_name] [solution_name] .jar file to download and run has been hardcoded via [company_name] [solution_name] property `DETECT_SOURCE` environment variable or the [company_name] [solution_name] version set by the `DETECT_LATEST_RELEASE_VERSION` or `DETECT_VERSION_KEY` variables, self update will not occur. These are optional System environment properties used by Detect upgrade scripts.

If the [blackduck_product_name] “Internally Hosted” option has been selected and a [company_name] [solution_name] download location has not been provided, the feature will not be enabled.

For further [blackduck_product_name] configuration information, refer to the documentation provided under the topic:
<xref href="DetectLocation.dita" scope="peer"> Hosting location for Synopsys Detect.
<data name="facets" value="pubname=bd-hub"/>

## [company_name] [solution_name] log examples for self update

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

Current version of [company_name] [solution_name] matches the mapped version or there is no mapped version in [blackduck_product_name]:   

```
2023-05-05 12:33:52 EDT INFO  \[main] - Detect-Self-Updater:  Checking https://test1.synopsys‎ .com/api/tools/detect API for centrally managed Detect version to download to /Users/testuser/tmp.  

2023-05-05 12:33:53 EDT INFO  \[main] - Detect-Self-Updater:  Present Detect installation is up to date - skipping download.
```
<!-- Variables do not resolve when in a note format hence the hardcoding below -->
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

