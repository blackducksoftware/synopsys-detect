# component-location-analysis

Enable this feature by adding --component.location.analysis.enabled=TRUE to a run of Detect.

When enabled, Synopsys Detect creates an output file in the scan subdirectory of the output directory with the name 'components-with-locations.json' which identifies the declaration locations (filepath, line number and column position) of open source components found in the source project.

* As Detect shuts down, by default, it performs cleanup operations which include deleting the component location analysis file. You can disable clean up by setting --detect.cleanup=false.

## Requirements and Limitations

* A limited subset of Tools can be run.
    * The currently supported tools are: DETECTOR.
* The currently supported scan modes are: Rapid/Stateless and offline.
    * Rapid Scan mode requires Black Duck policies.
        * Only components that violate policies will be included in the analysis. If no policies are violated or there are no defined policies, then component location analysis is skipped.

## Results

The location of each open source component found by the Detect run is searched and the location is included if found. When not found, no declaration location field will be present. Each component will have a name and version. Components may optionally have a higher-level grouping identifier, commonly referred to as a groupId, organization, or vendor. The metadata field is only populated in the case of a Rapid Scan, see below for details.

Unlike persistent scans, no data is stored on Black Duck and all scans are done transiently. These scans are primarily intended to be fast.


**BODY:**


The results are also printed in the logs:
```
{
      "sourcePath": "absolute/path/to/project/root",
      "globalMetadata": {},
      "componentList": [
           { 
           "groupId": The groupId of the Component if it has one,
           "artifactId": Component name,
           "version": Component version,
            "metadata": { Component relevant data populated only when running in Rapid Scan mode. }
            },
            .  
            .  
            .                   
            {
            "groupId": The groupId of the Component if it has one,
            "artifactId": Component name,
            "version": Component version,
            "metadata": { Component specific data populated only when in Rapid Scan mode. },
            "declarationLocation": [{
                "absolute/path/to/build/file": [{
                    "lineLocation": [{
                        "lineNumber": The line number where this Component's version was declared,
                        "columnPosition": [{
                            "colStart": The column start position where this Component's version was declared,
                            "colEnd": The column end position where this Component's version was declared
                        }]
                    }]
                }]
            }]
            }
        }
    ]
}
```

## Component Location Analysis - Rapid Scan Mode

Short and long term upgrade guidance, policy violations, etcWhen run in Rapid mode, the file looks like:
```
                  
{
      "sourcePath": "absolute/path/to/project/root",
      "globalMetadata": {},
      "componentList": [
      {
            "groupId": The groupId of the Component if it has one,
            "artifactId": Component name,
            "version": Component version,
            "metadata": { 
            
            <insert exact list>
            
            
            },
            "declarationLocation": [{
                "absolute/path/to/build/file": [{
                    "lineLocation": [{
                        "lineNumber": The line number where this Component's version was declared,
                        "columnPosition": [{
                            "colStart": The column start position where this Component's version was declared,
                            "colEnd": The column end position where this Component's version was declared
                        }]
                    }]
                }]
            }]
            }
        }
    ]
}
```