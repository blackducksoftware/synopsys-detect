# Solutions to common problems

## DETECT_SOURCE was not set or computed correctly

### Symptom

detect8.sh fails with: DETECT_SOURCE was not set or computed correctly, please check your configuration and environment.

### Possible cause

detect8.sh is trying to execute this command:
````
curl --silent --header \"X-Result-Detail: info\" https://sig-repo.synopsys.com/api/storage/bds-integrations-release/com/synopsys/integration/synopsys-detect?properties=DETECT_LATEST
````
The response to this command should be similar to the following:
```
{
"properties" : {
"DETECT_LATEST" : [ "https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect/5.6.1/synopsys-detect-5.6.1.jar" ]
},
"uri" : "https://sig-repo.synopsys.com/api/storage/bds-integrations-release/com/synopsys/integration/synopsys-detect"
}
```
When that command does not successfully return a value for property DETECT_LATEST, detect8.sh reports:
````
DETECT_SOURCE was not set or computed correctly, please check your configuration and environment.
````

### Solution

If the curl command described above does not successfully return a value for property DETECT_LATEST, you must determine why, and make the changes necessary so that curl command works.

## [solution_name] succeeds, but the results are incomplete because package managers or subprojects were overlooked

### Symptom

In this scenario, everything succeeds, but many or all components are missed. Examining the log shows that
package managers were not recognized and/or subprojects were overlooked.

### Possible cause

The detector search depth needs to be increased. The default value (0) limits the search for package manager files to the project directory. If project manager files
are located in subdirectories and/or there are subprojects, this depth should be increased to enable [solution_name] to find the relevant files, so it
will run the appropriate detector(s).

See [detector search depth](../properties/configuration/paths.md#detector-search-depth) for more details.

## [solution_name] fails and a TRACE log shows an HTTP response from [blackduck_product_name] of "402 Payment Required" or "502 Bad Gateway"

### Symptom

[solution_name] fails, and a TRACE log contains "402 Payment Required" or "502 Bad Gateway".

### Possible cause

[blackduck_product_name] does not have a required feature (notifications, binary analysis, etc.) enabled.

### Solution

Enable the required feature on the [blackduck_product_name] server.

## Unexpected behavior running [solution_name] on a project that uses Spring Boot

### Symptom

Unexpected behavior, and/or unexpected property values shown in the log.

### Possible cause

If your source directory contains Spring Framework configuration files named application.properties, application.yml,
or application.xml that are written for any application other than [solution_name], you should not run [solution_name] from your source directory.

### Solution

To prevent [solution_name] from reading those files, run [solution_name] from a different directory. Use the following property to point to your source directory.
```
--detect.source.path={project directory path}
```

## PKIX error connecting to [blackduck_product_name]

### Symptom

Exception: Could not communicate with [blackduck_product_name]: Could not perform the authorization request: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

### Possible cause

The [blackduck_product_name] server certificate is not in Java's keystore.

### Solution

1. Acquire the certificate file for your [blackduck_product_name] server.
1. Determine which *java* executable is being used to run [solution_name]. If you run [bash_script_name], that is either $JAVA_HOME/bin/java (the default) or the first *java* found on your $PATH.
1. Determine the Java home directory for that *java* executable.
1. Run [keytool](https://docs.oracle.com/en/java/javase/11/tools/keytool.html) to install the [blackduck_product_name] server certificate into the keystore in that Java home directory.

Although not recommended, it is possible to disable the certificate check with the [trust cert property](../properties/configuration/blackduck-server.md#trust-all-ssl-certificates-advanced).

## Not Extractable: NUGET - Solution INFO \[main\] -- Exception occurred: java.nio.file.InvalidPathException

### Symptom

Running [solution_name] on a NuGet project on Windows, a message similar to the following appears in the [solution_name] log:

````
Not Extractable: NUGET - Solution INFO [main] -- Exception occurred: java.nio.file.InvalidPathException: Illegal char
<:> at index 2: C:\...
````

### Possible cause

The value of $PATH contains a whitespace character after a semicolon and the path mentioned in the log message.

### Solution

Remove spaces immediately following semicolons in the value of $PATH.

## No project name/version provided or derived

### Symptom

Upload to [blackduck_product_name] fails with a message similar to the following in the log:

````
ERROR [main] -- createProject.arg0.name can't be blank [HTTP Error]: There was a problem trying to POST https://.../api/projects, response was 412 Precondition Failed.
````

### Possible cause

No project name and version were provided via properties and no [solution_name] tool capable of deriving a project name and version was included in the run. For example,
you will get this (or a similar) error if you run with --detect.tools.BINARY_SCANNER and do not set --detect.project.name or --detect.project.version.name.

### Solution

Set --detect.project.name and --detect.project.version.name.

## [blackduck_signature_scanner_name] fails on Alpine Linux

### Symptom

The [blackduck_signature_scanner_name] fails on Alpine Linux with an error similar to:

````
There was a problem scanning target '/opt/projects/myproject': Cannot run program "/home/me/blackduck/tools/Black_Duck_Scan_Installation/scan.cli-2020.6.0/jre/bin/java": error=2, No such file or directory
````

### Possible cause

The Java bundled with the [blackduck_signature_scanner_name] does not work on Alpine Linux (it relies on libraries not usually present on an Alpine system).

### Solution

Install an appropriate version of Java and tell [solution_name] to invoke the [blackduck_signature_scanner_name] using that
version of Java by setting environment variable BDS_JAVA_HOME to the JAVA_HOME value for that Java installation.

For example:

````
export BDS_JAVA_HOME=$JAVA_HOME
````

Or:

````
export BDS_JAVA_HOME=/usr/lib/jvm/java-11-openjdk/jre
````

## On Windows: Error trying cleanup

### Symptom

When running on Windows, inspecting a Docker image (e.g. using --detect.docker.image or --detect.docker.tar),
during shutdown, [solution_name] logs messages similar to the following:
````
2020-08-14 14:31:04 DEBUG [main] --- Error trying cleanup:

java.io.IOException: Unable to delete file: C:\Users\Administrator\blackduck\runs\2020-08-14-21-28-40-106\extractions
...
Caused by: java.nio.file.FileSystemException: C:\Users\Administrator\blackduck\runs\2020-08-14-21-28-40-106\extractions\DOCKER-0\application.properties: The process cannot access the file because it is being used by another process.
````

### Possible cause

This happens when Docker fails to release its lock on the volume mounted directory when it shuts down the image inspector service container
due to [Docker for Windows issue 394](https://github.com/docker/for-win/issues/394).
The result is that [solution_name] cannot fully clean up its output directory and leaves behind empty subdirectories.
The problem may be intermittent.

### Solution

There is no harm in leaving the directories behind in the short term but we recommend periodically removing them if the problem occurs frequently.
Restarting Docker will force Docker to release the locks, and enable you to remove the directories.

