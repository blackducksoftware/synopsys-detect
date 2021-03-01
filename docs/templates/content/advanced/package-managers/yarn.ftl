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
to build the complete graph of direct and transient dependencies. The Yarn detector
produces a single codelocation with this graph.

${solution_name} supports projects that use Yarn version 1 or version 2, that do not use Yarn workspaces.

## Yarn Workspace Support

${solution_name} supports [Yarn workspaces](https://yarnpkg.com/features/workspaces/#gatsby-focus-wrapper) in Yarn 2 projects (projects a yarn.lock file with __metadata.version value of 4).

${solution_name} does not support Yarn workspaces in Yarn 1 projects (projects with a yarn.lock file that is "v1" format).
