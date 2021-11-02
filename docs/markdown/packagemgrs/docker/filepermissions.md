# File permissions

When using Docker Inspector, [solution_name] must be run in an environment configured so that files created
by Docker Inspector are readable by all. On Linux, this means an appropriate umask value
(for example, 002 or 022 will work). On Windows, this means that the [solution_name]
output directory must be readable by all.

Docker image tarfiles passed to [solution_name] via the *detect.docker.tar* property must be readable by all.

