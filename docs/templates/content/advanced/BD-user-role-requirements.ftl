# ${blackduck_product_name} user role requirements

Overview:

* The Project Creator role is required to create ${blackduck_product_name} projects.
* The Global Code Scanner role or Project Code Scanner for the project is required to populate the project BOM.
* The BOM Manager role is required to generate reports.

Additional details:

* The Project Code Scanner role is insufficient to run any ${solution_name} post build actions; for example, policy check, notices report, risk report.
* A user with the Global Code Scanner role can (as of ${blackduck_product_name} 2019.6) generate a report, but cannot delete the report when it's complete. The BOM Manager role is required for the delete.
