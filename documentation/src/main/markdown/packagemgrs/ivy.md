# Ivy (Ant) support

## Overview

[solution_name] supports an Ivy detector to extract dependency information for projects that use Ivy, a common dependency manager for Ant projects.

[solution_name] runs the Ivy detector if it finds a ivy.xml file in your project.

The Ivy detector parses the ivy.xml file for information on your project's dependencies.
The Ivy detector extracts the project's name and version from the build.xml file.  If it does not find a build.xml file, it will defer to values derived by git, from the project's directory, or defaults.
