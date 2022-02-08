# Positioning [solution_name] in the build process

## Build mode

In [build mode](../../components/detectors.md#build-detectors-versus-buildless-detectors), which is the default,
[solution_name] should be executed as a post-build step in the build environment of the project.
Building your project prior to running [solution_name] is often required for the detector to run successfully,
and helps ensure that the build artifacts are available for signature scanning.

## Buildless mode

In [buildless mode](../../components/detectors.md#build-detectors-versus-buildless-detectors),
[solution_name] makes its best effort to discover dependencies without the benefit of
build artifacts or build tools. In buildless mode, there is no requirement that [solution_name] must run as a post-build step.
Results from buildless mode may be less accurate than results from build mode.
