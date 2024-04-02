# Threat Intel Scan

Threat Intel Scan is a way of running a scan of binary files and providing malware warnings and risk analysis.

[company_name] [solution_name] will accept a user provided local file path to a binary file.

Identification of malware and other threats will be displayed to [blackduck_product_name] users at the component level, and will include file name, product (component name), product version, and publisher if identified.

Threat Intel Scan supports persistent (Intelligent) scan modes in [blackduck_product_name].

## Requirements and Limitations

### General Requirements
 * Your [blackduck_product_name] server must have [blackduck_product_name] .
 * Must have Match as a Service (MaaS) licensed, and enabled within [blackduck_product_name].
 * A unique project version must be provided, or the scan service will respond with an error.
 * Must be running [blackduck_product_name] 2024.4.0 or greater.
 * Threat Intel Scans require network connectivity (Air gap mode is not supported).
 
### Limitations
 * Threat Intel Scan is limited to images of 5GB or less for hosted services.
 * Threat Intel Scan is limited to images of 6GB or less for local, on-prem services.
 
## Invocation
 * To invoke a threat intel scan, which executes in "Intelligent" mode by default, the following must be provided at a minimum:   
 ```
--detect.tools=THREAT_INTEL
--detect.container.scan.file.path=<Path to local binary file>
```

## Results

Threat intel scan findings will appear in the [blackduck_product_name] user interface, please consult the documentation provided by [blackduck_product_name].
<!-- TBD Reference link directly to [blackduck_product_name] Docs once they are available
<xref href="threatintelscans.dita" scope="peer">Threat Intel Scans
<data name="facets" value="pubname=bd-hub"/>
and image
<figure>
<img src="images/threatintelscans.png"
         alt="Threat Intel Scan Results">
    <figcaption>Threat Intel Scan Results.</figcaption>
</figure>
-->
