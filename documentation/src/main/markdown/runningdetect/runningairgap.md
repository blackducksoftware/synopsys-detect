# Running [solution_name] in air gap mode

Running [solution_name] in air gap mode requires adding scanners, inspectors, and associated libraries to a jar file such that they can be executed without network access.

<note type="note">Air Gap mode is not the same as running in Offline mode. Air Gap mode allows for scanning when operating in an air gapped environment and relies on a jar file that contains the required scanning libraries. Offline mode is used when you do not wish to download scanners or upload results files directly to [blackduck_product_name], but still supports the use of a local signature scanner instance.</note>

## Adding the [blackduck_signature_scanner_name] to your air gap archive

To create an air gap archive that includes the [blackduck_signature_scanner_name], follow these steps:

1. Unzip the [solution_name] air gap archive to create the [solution_name] air gap directory.
1. Download the appropriate [blackduck_signature_scanner_name] zip file from your Black Duck instance (System > Tools > Legacy Downloads > Signature Scanner), and unzip it. This will create a directory with a name like scan.cli-x.y.z.
1. Move that scan.cli-x.y.z directory to the top level of the [solution_name] air gap directory.
1. Zip the enhanced [solution_name] air gap directory to create your enhanced air gap archive.

When you later run [solution_name] from the directory created by unzipping your enhanced air gap archive, set property detect.blackduck.signature.scanner.local.path to the path to the scan.cli-x.y.z directory in your enhanced air gap archive directory.

## Preparing and running in air gap mode

To prepare to run [solution_name] in air gap mode, unzip the air gap archive to create the air gap directory.
Do not make changes to files in the air gap directory.
Invoke the [solution_name] .jar file from its original unzipped location at the top level of the air gap directory.
For more information on invoking the .jar file, refer to [Running the Synopsys Detect .jar](../runningdetect/basics/runningjar.md).
