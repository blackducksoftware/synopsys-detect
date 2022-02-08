# Inspecting Windows Docker images

Given a Windows Image, Docker Inspector, since it can only discover packages using
a Linux package manager, will not contribute any components to the BOM, but will
return the container filesystem (in the form of a squashed image),
which [solution_name] will scan using the [blackduck_signature_scanner_name].

