# Risk Report Generation

${solution_name} can generate a Black Duck risk report in PDF form.
${solution_name} looks for risk report generation details in the properties whose names start with detect.risk.report,
including:

* detect.risk.report.pdf (enable report generation)
* detect.risk.report.path (path where report will be located)

If ${solution_name} generates a risk report, you must either be online or have the applicable air gap files in order to render the PDF properly.

If you are online, then Synopsys Artifactory is used to download the applicable font file which is then unzipped and the font files are located.
If you are offline, the air gap font files are located at the provided path.
