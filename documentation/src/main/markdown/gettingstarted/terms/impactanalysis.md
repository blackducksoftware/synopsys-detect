# Vulnerability Impact Analysis

When the *detect.impact.analysis.enabled* property in [detect_product_short] to set to true, [detect_product_short] creates a call graph (a list of calls made by your code)
to understand the public methods your code is using in your application.
The call graph shows the fully qualified public method names as well as the line number where the function was called.
The data is packaged into a single file and [detect_product_short] sends this file over HTTPS to the [bd_product_short] server, enabling vulnerability impact analysis in [bd_product_short]
