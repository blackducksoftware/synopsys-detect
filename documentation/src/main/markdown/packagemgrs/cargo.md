# Cargo support

## Overview

[solution_name] runs the Cargo detector if it finds either of the following files in your project:

* Cargo.toml
* Cargo.lock

The Cargo detector parses the Cargo.lock file for information on your project's dependencies. If the detector discovers a Cargo.toml file but not a Cargo.lock file, it will prompt the user to generate a Cargo.lock by running `cargo generate-lockfile` and then run [solution_name] again.
The Cargo detector extracts the project's name and version from the Cargo.toml file.  If it does not find a Cargo.toml file, it will defer to values derived by Git, from the project's directory, or defaults.
