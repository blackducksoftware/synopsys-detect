# Air Gap Mode

To run [solution_name] on an air-gapped computer or network, you must first download and install
files that [solution_name] normally downloads as it runs. These include inspectors
for Docker, Gradle, NuGet and more.

## Downloading or creating an air gap zip

Air gap archives are available for download from the location specified in [download locations](downloadlocations.md).

As an alternative, you can create an air gap archive by running [solution_name] with the
-z or --zip command line option.
Optionally you can follow --zip with a space and an argument (for example: --zip FULL) to customize the air gap zip. Possible values: FULL (produce a full air gap zip; the default), NO_DOCKER (do not include the Docker Inspector).
The archive created contains the [solution_name] .jar and the inspectors.

## Running in air gap mode

To prepare to run [solution_name] in air gap mode, unzip the air gap archive to create the air gap directory.
Do not make any changes to files within that directory.
Invoke the [solution_name] .jar file from its original unzipped location at the top level of the air gap directory.
Refer to [Running the Synopsys Detect .jar](basics/runningjar.md) for more information on invoking the .jar file.

## Adding the [blackduck_signature_scanner_name] to your air gap zip

If you want an air gap zip that includes the [blackduck_signature_scanner_name], you can create it as follows:

1. Unzip the [solution_name] air gap zip to create the [solution_name] air gap directory.
1. Download the appropriate [blackduck_signature_scanner_name] zip from your Black Duck instance (System > Tools > Legacy Downloads > Signature Scanner), and unzip it. This will create a directory with a name like scan.cli-x.y.z.
1. Move that scan.cli-x.y.z directory to the top level of your [solution_name] air gap directory.
1. Zip the enhanced [solution_name] air gap directory to create your enhanced air gap zip.

When you later run [solution_name] from the directory created by unzipping your enhanced air gap zip, set property detect.blackduck.signature.scanner.local.path to the path to the scan.cli-x.y.z directory within your enhanced air gap zip directory.
