# Vulnerability Impact Analysis

When the *detect.impact.analysis.enabled* property in [solution_name] to set to true, [solution_name] creates a call graph (a list of calls made by your code)
to understand the public methods your code is using in your application.
The call graph shows the fully qualified public method names as well as the line number where the function was called.
The data is packaged into a single file and [solution_name] sends the file over HTTPS to the [blackduck_product_name] server, enabling vulnerability impact analysis in [blackduck_product_name]
