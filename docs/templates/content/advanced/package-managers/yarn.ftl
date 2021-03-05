# Yarn support

${solution_name} runs the Yarn detector if it finds both of the following files in your project:

* yarn.lock
* package.json

The Yarn detector reads both files to derive project and dependency information.
The package.json file specifies the direct dependencies for the project. ${solution_name} adds these
dependencies to the top level of the dependency graph that it builds.
The yarn.lock file contains necessary details about those
direct dependencies and their transient dependencies, enabling ${solution_name}
to build the complete graph of direct and transient dependencies. The Yarn detector
produces a single codelocation with this graph.

${solution_name} supports projects that use Yarn version 1 or version 2.

## Yarn Workspace Support

${solution_name} supports Yarn projects that use workspaces. There are some differences in the way ${solution_name} handles workspaces between Yarn 1
projects and Yarn 2 projects, due to limitations in workspace-related information available in Yarn 1 yarn.lock files.

### Workspace support in Yarn 2 projects

In Yarn 2 projects (yarn.lock file format version 4), the yarn.lock file defines the full dependency graph for the project, including:

* The root project's dependencies on both components and workspaces
* The workspaces' dependencies on both components and workspaces
* Components' dependencies on other components

By default, ${solution_name} will use that information to produce a dependency graph (in the output BDIO) that accurately reflects the root project's
dependencies, including workspaces. If, instead, you want ${solution_name} to include every workspace in the project (whether the root
project actually depends on it or not),
you also have the option to include *all* workspaces (and their dependencies) in the graph by setting
property *detect.yarn.include.all.workspaces* to true.

### Workspace support in Yarn 1 projects

In Yarn 1 projects (yarn.lock file format version 1), the yarn.lock file contains neither a definition of the root project,
nor a definition of the workspaces.
As a result, there's no way for ${solution_name} to determine which workspaces the root project actually depends on.
So for Yarn 1 projects, ${solution_name} always includes all workspaces in the produced dependency graph.
The value of property *detect.yarn.include.all.workspaces* has no effect.

TODO: This isn't really true, is it?? Shouldn't it (by default) only add those workspaces declared as dependencies
in the package.json (and use the workspace package.json to determine it's dependencies)?
Property *detect.yarn.include.all.workspaces* should actually work just like it does for yarn 2 projects.

