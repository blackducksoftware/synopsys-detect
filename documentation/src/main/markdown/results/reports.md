# Risk Report Generation

[company_name] [solution_name] can generate a [blackduck_product_name] risk report in PDF format.
[company_name] [solution_name] looks for risk report generation details in the properties whose names start with detect.risk.report, including:

* detect.risk.report.pdf (enable report generation by setting to "true")
* detect.risk.report.path (path where report will be located)

## Fonts

Default font files are used to create the risk report pdf. 

You may specify a custom regular font and/or a custom bold font by placing a .ttf font file in a directory called "custom-regular" and/or "custom-bold", respectively, that is a child to the directory at ```detect-output-directory/tools/fonts```, where 'detect-output-directory' is determined by [detect.output.path](../properties/configuration/paths.md#detect-output-path)

Examples

* ```/path-I-passed-to-detect-output-path/tools/fonts/custom-regular/my-custom-regular-font.ttf```
* ```/Users/user/blackduck/tools/fonts/custom-regular/my-custom-regular-font.ttf``` on Unix
* ```C:\Users\blackduck\tools\fonts\custom-bold\my-custom-bold-font.ttf``` on Windows

## File Naming

When generating the risk report file, non-alphanumeric characters separating portions of the project name or version will be replaced with underscores. For example, in a case with hyphens and periods like "Project-Name" and "Project.Version.Name", the resulting file name would be ```Project_Name_Project_Version_Name_BlackDuck_RiskReport.pdf```

### Air Gap

Normally font files used in creating the risk report pdf are downloaded from Artifactory. If you are using the [company_name] [solution_name] air gap, the font files are retrieved from a directory called 'fonts' that is a child to the root of the air gap directory.

To specify custom fonts when using the [company_name] [solution_name] air gap zip, you must unzip the produced airgap zip file and then place a .ttf font file in a directory called "custom-regular" and/or "custom-bold" that is a child to the directory airGapRoot/fonts.

Example

* ```synopsys-detect-detect_version-air-gap/fonts/custom-regular/my-custom-regular-font.ttf```
