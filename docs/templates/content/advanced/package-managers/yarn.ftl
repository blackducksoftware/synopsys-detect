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

${solution_name} supports Yarn projects that use workspaces. By default, only workspaces that are dependencies of the
root project (directly or indirectly) are included in the produced dependency graph.
If you set property *detect.yarn.include.all.workspaces* to true,
*all* workspaces will be included in the results, whether they are dependencies of the root project or not.