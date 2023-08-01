# Component Location Analysis LCA

Enable this feature by adding --detect.component.location.analysis.enabled=TRUE to a run of Detect.

When enabled, [solution_name] creates an output file in the scan subdirectory of the output directory with the name 'components-with-locations.json' which identifies the declaration locations (filepath, line number and column position) of the version of open source components found in the scanned project.

* As Detect shuts down, by default, it performs cleanup operations which include deleting the component location analysis file. You can disable clean up by setting --detect.cleanup=false.

## Requirements and Limitations

* Component Location Analysis is available under [solution_name] “Limited Customer Availability (LCA)”.
* A limited subset of Detector Types support this feature.
    * The currently supported package managers as of 8.11.0 are: NPM, Maven, Gradle and NuGet.
* The currently supported scan modes as of 8.11.0 are: Rapid/Stateless and offline.
    * Rapid/Stateless Scan mode requires Black Duck policies.
        * Only components that violate policies will be included in the analysis. If no policies are violated or there are no defined policies, then component location analysis is skipped.
    * Offline mode
      * When enabled for a scan without Black Duck connectivity, all detected open source components will be included in the location analysis results.

## Offline Mode Results

Each component is uniquely identified by a name and version. Components may optionally have a higher-level grouping identifier, commonly referred to as a groupId, organization, or vendor. The declaration location of each component is included in the results if found. When not found, no declarationLocation field will be present for that component in the output file. The metadata field is only populated in the case of a Rapid Scan, see Rapid Scan Mode Results below for details.


**BODY:**
```
{
      "sourcePath": "absolute/path/to/project/root",
      "globalMetadata": { Optional project relevant data. },
      "componentList": [
           { 
               "groupID": The groupId of the Component if it has one,
               "artifactID": Component name,
               "version": Component version,
               "metadata": { Component relevant data populated only when running in Rapid Scan mode. }
            },
            .  
            .  
            .                   
            {
                "groupID": The groupId of the Component if it has one,
                "artifactID": Component name,
                "version": Component version,
                "metadata": { Component specific data populated only when in Rapid Scan mode. },
                "declarationLocation": {
                    "fileLocations": [
                      {
                        "filePath": "relative/path/to/build/file",
                        "lineLocations": [
                          {
                            "lineNumber": The line number where this Component's version was declared,
                            "columnLocations": [
                              {
                                "colStart": The column start position where this Component's version was declared,
                                "colEnd": The column end position where this Component's version was declared
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

## Rapid/Stateless Scan Mode Results

When Detect runs a Rapid or Stateless scan, the output file includes policy violation vulnerabilities, component violating policies and remediation guidance (short term, long term and transitive upgrade guidance) when available. This information is contained within the metadata field of each component:
```
{
      "sourcePath": "absolute/path/to/project/root",
      "globalMetadata": {},
      "componentList": [
        {
            "groupID": The groupId of the Component if it has one,
            "artifactID": Component name,
            "version": Component version,
            "metadata": {
                "policyViolationVulnerabilities": [],
                "shortTermUpgradeGuidance": {},
                "longTermUpgradeGuidance": {},
                "transitiveUpgradeGuidance": [],
                "componentViolatingPolicies": []
            },
            "declarationLocation": {
                "fileLocations": [
                  {
                    "filePath": "relative/path/to/build/file",
                    "lineLocations": [
                      {
                        "lineNumber": The line number where this Component's version was declared,
                        "columnLocations": [
                          {
                            "colStart": The column start position where this Component's version was declared,
                            "colEnd": The column end position where this Component's version was declared
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
