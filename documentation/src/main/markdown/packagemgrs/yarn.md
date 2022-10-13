# Yarn support

## Related properties

[Detector properties](../properties/detectors/yarn.md)

## Overview

[solution_name] runs the Yarn detector if it finds both of the following files in your project:

* yarn.lock
* package.json

The Yarn detector reads both files (see *Yarn workspace support* below for information on
other files the Yarn detector might read)
to derive project and dependency information.
The yarn.lock file must be up-to-date before [solution_name] runs.
The package.json file specifies the direct dependencies for the project. [solution_name] adds these
dependencies to the top level of the dependency graph that it builds.
The yarn.lock file contains necessary details about those
direct dependencies and their transient dependencies, enabling [solution_name]
to build the complete graph of direct and transient dependencies.

[solution_name] supports projects that use Yarn version 1 or version 2.

## Yarn workspace support

In addition to the codelocation generated for the project (showing its direct
and transitive dependencies),
[solution_name] also generates a codelocation per included workspace
(all workspaces are included by default).

### Referencing workspaces

When you use the workspace exclude/include properties, refer to workspaces
the same way Yarn refers to them in the *workspaces* list in the declaring package.json file:
use the relative path of the workspace directory (relative to the declaring workspace).

For example, if your project package.json contains:
````
"workspaces": [
"packages/workspace-a",
"packages/workspace-b"
],
````
[solution_name] will expect you to refer to these workspaces as "packages/workspace-a" and "packages/workspace-b".
This naming convention remains the same even at deeper workspace nesting levels. As an example, if your project
has a workspace packages/workspace-a, and packages/workspace-a's package.json contains:
````
"workspaces": [
"child1-of-workspace-a",
"child2-of-workspace-a"
],
````
[solution_name] will expect you to refer to these workspaces as "child1-of-workspace-a" and "child2-of-workspace-a".

### Excluding workspaces

By default, [solution_name] includes all workspaces in the results regardless of whether they
are declared as dependencies of the project.
You can specify a subset of workspaces to include or exclude using the workspace exclude/include properties
*detect.yarn.excluded.workspaces* and *detect.yarn.included.workspaces*.

When using the workspace exclude and include properties, use the workspace
referencing guidelines described above. You can also use
filename globbing-style wildcards and specify multiple values separated
by commas.
