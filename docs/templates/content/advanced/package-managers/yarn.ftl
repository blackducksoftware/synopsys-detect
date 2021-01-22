# Yarn support

${solution_name} runs the Yarn detector if it finds both of the following files in your project:

* yarn.lock
* package.json

The Yarn detector reads both files to derive project and dependency information.
The package.json file specifies the direct dependencies for the project. ${solution_name} adds these
dependencies to the top level of the dependency graph that it builds.
The yarn.lock file contains necessary details about those
direct dependencies and all transient dependencies. The details provided by the yarn.lock file
about each dependency include its (transient) dependencies, enabling ${solution_name}
to build the complete graph of direct ind transient dependencies. The Yarn detector
produces a single codelocation with this graph.

## Yarn Workspace Support

${solution_name} does not currently support projects that use Yarn workspaces as defined [here](https://classic.yarnpkg.com/en/docs/workspaces/).