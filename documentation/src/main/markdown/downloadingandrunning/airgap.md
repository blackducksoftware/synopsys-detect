# Air Gap Mode

To run [solution_name] on an air-gapped computer or network, you must first download and install
[solution_name] and files ([solution_name] dependencies) that [solution_name] normally downloads as it runs.
These include inspectors for Docker and NuGet, libraries that the Gradle inspector requires, etc. These files are packaged together
in an air gap archive (zip file).

## Downloading or creating an air gap archive

Air gap archives are available for download from the location specified in [download locations](downloadlocations.md).
These air gap archives contain the versions of the dependencies that were current at the time of the [solution_name] release. 

As an alternative, you can create an air gap archive yourself.
An air gap archive that you create will contain the versions of the dependencies that are current at the time you create the air gap archive
(the same versions [solution_name] would download if run at that time).

To create an air gap archive, run [solution_name] with the
-z or --zip command line option.
Optionally you can follow --zip with a space and an argument (for example: --zip FULL) to customize the air gap zip. Possible values: FULL (produce a full air gap zip; the default), NO_DOCKER (do not include the Docker Inspector).

Your PATH environment variable must include the *bin* directory of the Gradle distribution to generate an Air Gap archive.

## Running in air gap mode

To prepare to run [solution_name] in air gap mode, unzip the air gap archive to create the air gap directory.
Do not make changes to files in the air gap directory.
Invoke the [solution_name] .jar file from its original unzipped location at the top level of the air gap directory.
For more information on invoking the .jar file, refer to [Running the Synopsys Detect .jar](basics/runningjar.md).

## Adding the [blackduck_signature_scanner_name] to your air gap archive

To create an air gap archive that includes the [blackduck_signature_scanner_name], follow these steps:

1. Unzip the [solution_name] air gap archive to create the [solution_name] air gap directory.
1. Download the appropriate [blackduck_signature_scanner_name] zip file from your Black Duck instance (System > Tools > Legacy Downloads > Signature Scanner), and unzip it. This will create a directory with a name like scan.cli-x.y.z.
1. Move that scan.cli-x.y.z directory to the top level of the [solution_name] air gap directory.
1. Zip the enhanced [solution_name] air gap directory to create your enhanced air gap archive.

When you later run [solution_name] from the directory created by unzipping your enhanced air gap archive, set property detect.blackduck.signature.scanner.local.path to the path to the scan.cli-x.y.z directory in your enhanced air gap archive directory.
