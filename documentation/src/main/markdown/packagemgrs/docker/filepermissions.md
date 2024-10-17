# File permissions

When using Docker Inspector, [detect_product_short] must be run in an environment configured so that files created
by Docker Inspector are readable by all. On Linux, this means an appropriate umask value
(for example, 002 or 022 will work). On Windows, this means that the [detect_product_short]
output directory must be readable by all.

Docker image tarfiles passed to [detect_product_short] via the *detect.docker.tar* property must be readable by all.

