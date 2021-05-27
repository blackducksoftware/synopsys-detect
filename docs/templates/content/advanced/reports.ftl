# Risk Report Generation

${solution_name} can generate a Black Duck risk report in PDF form.
${solution_name} looks for risk report generation details in the properties whose names start with detect.risk.report,
including:

* detect.risk.report.pdf (enable report generation)
* detect.risk.report.path (path where report will be located)

If ${solution_name} generates a risk report, you must either be online or have the applicable air gap files in order to render the PDF properly.

If you are online, then an applicable font file is downloaded from Artifactory.
You may specify a custom regular font and/or a custom bold font by placing a .ttf font file in a directory called "custom-regular" and/or "custom-bold", respectively, that is a child to the directory at ${solution_name}-output-directory/tools/fonts, where '${solution_name}-output-directory' is one of the following two paths:
* The path provided to [detect.output.path](../../proprties/)
* home/blackduck where 'home' is the path derived from the system property [user.home](https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html)

If you are offline, the air gap font files are located at the provided path.
To specify custom fonts when using the ${solution_name} air gap zip, you must unzip the produced airgap zip file, and then place a .ttf font file in a directory called "custom-regular" and/or "custom-bold" that is a child to the directory airGapRoot/fonts.