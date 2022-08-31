# Positioning [solution_name] in the build process

Ideally
[solution_name] should be executed as a post-build step in the build environment of the project.
Building your project prior to running [solution_name] is required for many detectors to run successfully,
tends to produce the most accurate results, and helps ensure that the build artifacts are available for signature scanning.

When higher accuracy detectors are unable to run (due to, for example, the absence of package manager executables they need),
and a lower accuracy detector is also available,
[solution_name] makes its best effort to discover dependencies by running the lower accuracy detector.

See [Detector cascade](../detectorcascade.md) for more information.
