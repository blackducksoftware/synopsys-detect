# Status File

As of detect 6.2.0 an output status file is created in the run folder with the name "status.json" and is meant to summarize the detect run in a machine readable format.

The file includes status codes, issues encountered and results produced. As additional processes consume this file, additional information will be added. The format is intended to evolve over time.

* As Detect shuts down, by default, it performs cleanup operations which include deleting the status file.  You can disable clean up by setting ```--detect.cleanup=false```.

##Body
```
{
"formatVersion": The version of the status file format. Will change as new features are introduced.
"detectVersion": The version of ${solution_name} that created the status file.
"projectName": The project name.
"projectVersion": The project version.
"detectors": [ List of Detectors, see details below. ]
"status": [ List of Status, see details below. ]
"issues": [ List of Issues, see details below. ]
"results": [ List of Results, see details below. ]
"unrecognizedPaths": [ List of Unrecognized Paths, see details below. ]
"codeLocations": [ List of code locations produced, see details below. ]
"propertyValues": { An object representing all provided properties, see details below. }
}
```

##Detector
```
{
"folder": The folder the detector applied to.
"detectorType": The normalized detector type such as "GIT".
"detectorName": A shorthand name of the detector such as "Git Cli".
"descriptiveName": The long form name of the detector such as "GIT - Git Cli".
"discoverable":  A boolean indicating whether or not the detector was able to discover project information.
"extracted": A boolean indicating whether or not the detector was able to extract dependencies.
"status": An enum indicating whether the detector was successful, failed, or deferred to another detector.
"statusCode": A code specifying the nature of the detector's failure, or PASSED if the detector was successful. See below for a complete list of possible status codes.
"statusReason": A human readable description of the status code.
"relevantFiles": [ A list of files relevant to the detector. ]
"discoveryReason": A human readable description of the discovery result.
"extractedReason": A human readable description of the extraction result.
"projectName": The project name this detectable found.
"projectVersion": The project version this detectable found.
"codeLocationCount": The number of code locations this detector produced.
"explanations": [ A human readable list of strings describing why this detector ran such as "Found file: <path>". ]
}
```
###Detector Status Codes

| Status Code | Description |
| --- | --- |
<#list statusCodes as statusCode>
    | ${statusCode.statusCode} | ${statusCode.statusCodeDescription} |
</#list>

##Status
```
{
"key": The normalized key this status element describes such as "GIT".
"status": "SUCCESS" or "FAILURE"
}
```

##Issues
```
{
"type": A key describing the type of issue, currently "EXCEPTION", "DEPRECATION" or "DETECTOR".
"messages": A list of a strings describing the issue.
}
```

##Results

A result is a URL or file path to output produced by the ${solution_name} run: a Black Duck Bill Of Materials, Risk Report, Notices Report, or Air Gap zip.
```
{
"location": The path to the result.
"message": A string describing the result.
}
```

##Property Values

A map of every property key to it's string value that detect found. These are only properties to which detect has a known key, 
so pass-through properties like docker and dynamic properties like custom fields are not included. Passwords and other sensitive fields are masked.

```
  "propertyValues": {
    "key": "value",
    "boolean-key": "true"
  }
```

##Unrecognized Paths

For those detectors that support it (currently, only CLANG), a list of file paths to dependencies that
(a) were not recognized by the package manager, and (b) reside outside the source directory.
````
{
"
<Detector type>": [ A list of file paths to unrecognized dependencies ]
    }
    ````

    ## Code Locations
    ````
    {
    "codeLocationName": The name of a code location produced by this run of ${solution_name}.
    }
    ````
