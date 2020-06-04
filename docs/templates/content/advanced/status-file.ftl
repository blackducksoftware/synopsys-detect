# Status File

As of detect 6.2.0 an output status file is created in the run folder with the name "status.json" and is meant to summarize the detect run in a machine readable format.

The file includes status codes, issues encountered and results produced. As additional processes consume this file, additional information will be added. The format is intended to evolve over time.

##Body
```
{
    "formatVersion": The version of the status file format. Will change as new features are introduced.
    "detectVersion": The version of detect that created the status file.
    "detectors": [ List of Detectors, see format below. ]
    "status": [ List of Status, see format below. ]
    "issues": [ List of Issues, see format below. ]
    "results": [ List of Results, see format below. ]
    "unrecognizedPaths": [ List of Unrecognized Paths, see format and details below. ]
}
```

##Detector
```
{
    "folder": The folder the detector applied to.
    "detectorType": The normalized detector type such as "GIT".
    "detectorName": A shorthand name of the detector such as "Git Cli".
    "descriptiveName": The long form name of the detector such as "GIT - Git Cli".
    "searchable": A boolean of whether or not the detector searched the given folder (currently will always be true).
    "applicable": A boolean of whether or not the detector applied to the given folder (currently will always be true).
    "extractable":  A boolean indicating whether the detector could extract, meaning all of it's dependencies were downloaded or available.
    "discoverable":  A boolean indicating whether or not the detector was able to discover project information.
    "extracted": A boolean indicating whether or not the detector was able to extract dependencies.
    "searchableReason": A human readable description of the searchable result.
    "applicableReason": A human readable description of the applicable result.
    "extractableReason": A human readable description of the extractable result.
    "relevantFiles": [ A list of files relevant to the detector. ]
    "projectName": The project name this detectable found.
    "projectVersion": The project version this detectable found.
    "codeLocationCount": The number of code locations this detector produced.
}
```

##Status
```
{
    "key": The normalized key this status element describes such as "GIT".
    "status": "SUCCESS" or "FAILURE"
}
```

##Results
```
{
    "type": A key describing the type of issue, currently "EXCEPTION", "DEPRECATION" or "DETECTOR".
    "messages": A list of a strings describing the issue.
}
```

##Unrecognized Paths

For those detectors that support it (currently, only CLANG), a list of file paths to dependencies that
(a) were not recognized by the package manager, and (b) reside outside the source directory.
````
{
    "<Detector type>": [ A list of file paths to unrecognized dependencies ]
}
````