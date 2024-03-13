# Positioning [company_name] [solution_name] in the build process

For best results, execute [company_name] [solution_name] post-build step in the build environment of the project.
Building your project prior to running [company_name] [solution_name] is required for many detectors to run successfully,
tends to produce the most accurate results, and helps ensure that the build artifacts are available for signature scanning.

When higher accuracy detectors are unable to run (due to, for example, the absence of package manager executables they need),
and a lower accuracy detector is also available,
[company_name] [solution_name] makes its best effort to discover dependencies by running the lower accuracy detector.

See [Detector search and accuracy](../detectorcascade.md) for more information.
