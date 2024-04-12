# Component Location Analysis LCA

Enable this feature by adding --detect.component.location.analysis.enabled=TRUE to a run of [company_name] [solution_name].

When enabled, [company_name] [solution_name] creates an output file in the scan subdirectory of the output directory with the name 'components-with-locations.json' which identifies the declaration locations (filepath, line number and column position) of the version of open source components found in the scanned project.

<note type="note">By default, when [company_name] [solution_name] shuts down, it performs cleanup operations which include deleting the component location analysis file. You can disable clean up by setting `--detect.cleanup=false`.</note>

## Requirements and Limitations

* Component Location Analysis is available under [company_name] [solution_name] “Limited Customer Availability (LCA)”.
* A limited subset of Detector Types support this feature.
    * Detectors supported as of 9.2.0 are: NPM, MAVEN, GRADLE, NUGET, GO_MOD, GO_DEP, GO_GRADLE, GO_VENDOR, GO_VNDR, PIP, POETRY, and CONDA.
* Scan modes supported as of 8.11.0 are: Offline and Rapid/Stateless.
    * Offline mode
      * When enabled for a scan without [blackduck_product_name] connectivity, all detected open source components will be included in the location analysis results.
    * Rapid/Stateless Scan mode requires [blackduck_product_name] policies.
        * Only components that violate policies will be included in the analysis. If no policies are violated or there are no defined policies, then component location analysis is skipped.

## Offline Mode Results

Each component is uniquely identified by a name and version. Components may optionally have a higher-level grouping identifier, commonly referred to as a groupId, organization, or vendor. The declaration location of each component is included in the results if found. When not found, no declarationLocation field will be present for that component in the output file. 

<note type="note">The metadata field is only populated in the case of a Rapid Scan. See [Rapid or Stateless Scan Mode Results](#rapid-or-stateless-scan-mode-results)</note>

**Example results BODY:**
```
{
    "sourcePath": "/absolute/path/to/project/root",
    "globalMetadata": {},                                 // Passthrough data from producer to consumer (optional)
    "componentList": [
        { 
            "groupID": "org.sonarqube",                   // Component group (if available)
            "artifactID": "org.sonarqube.gradle.plugin",  // Component name
            "version": "2.8",                             // Component version
            "metadata": {                                 // Passthrough upgrade guidance data (unavailable in offline scan)
                "policyViolationVulnerabilities": [],     // (if available)
                "shortTermUpgradeGuidance": {},           // (if available)
                "longTermUpgradeGuidance": {},            // (if available)
                "transitiveUpgradeGuidance": [],          // (if available)
                "componentViolatingPolicies": []          // (if available)
            },
            "declarationLocation": {                      // Included if the component was located
                "fileLocations": [
                    {
                        "filePath": "build-script/build.gradle.kts",
                        "lineLocations": [
                            {
                                "lineNumber": 12,
                                "columnLocations": [
                                    {
                                        "colStart": 63,
                                        "colEnd": 65
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
    ]
}
```

## Rapid or Stateless Scan Mode Results

When [company_name] [solution_name] runs a Rapid or Stateless scan, the output file includes policy violation vulnerabilities, component violating policies and remediation guidance (short term, long term and transitive upgrade guidance) when available. This information is contained within the metadata field of each component.
