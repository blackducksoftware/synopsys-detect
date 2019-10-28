# Black Duck user role requirements

Overview:
* Project Creator role is required to create Black Duck projects.
* Global Code Scanner is required to populate the project BOM.
* BOM Manager is required to generate reports.

Additional details:
* The Project Code Scanner role is insufficient to run any detect post build actions (policy check, notices report, risk report)
* A user with the Global Code Scanner role can (as of 2019.6) run generate a report, but cannot delete the report when it’s done with it. BOM Manager role is required for the delete.