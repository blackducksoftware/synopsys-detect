# Yarn support

${solution_name} runs the Yarn detector if it finds both of the following files in your project:

* yarn.lock
* package.json

The Yarn detector reads both files (see *Yarn workspace support* below for information on
other files the Yarn detector might read)
to derive project and dependency information.
The yarn.lock file must be up-to-date before ${solution_name} runs.
The package.json file specifies the direct dependencies for the project. ${solution_name} adds these
dependencies to the top level of the dependency graph that it builds.
The yarn.lock file contains necessary details about those
direct dependencies and their transient dependencies, enabling ${solution_name}
to build the complete graph of direct and transient dependencies.

${solution_name} supports projects that use Yarn version 1 or version 2.

## Yarn workspace support

${solution_name} supports Yarn projects that use workspaces.
In addition to the codelocation generated for the project (showing its direct
and transitive dependencies),
${solution_name} also generates a codelocation per included workspace.
All workspaces are included by default. You can specify a subset of workspaces
to include using the workspace exclude/include properties.

Each workspace must be declared as a workspace in the project package.json
(or in the package.json of a workspace of the project). Whether or not a workspace
is declared as a dependency of the project it makes no difference
to ${solution_name}. Because workspaces
are included whether or not they are declared as dependencies,
${solution_name} operation is the same on monorepos as
on non-monorepo projects; what matters is which workspaces
are excluded/included via the workspace exclude/include properties.

## Using the workspace exclude and include filters

When using the workspace exclude and include filters, specify workspaces by
the relative path (relative to the project directory) of the workspace directory
(similar to the way they are
specified in the project package.json workspaces field).
For example, if your project
package.json's workspaces list includes "packages/workspace-a" and "packages/workspace-b",
you would include or exclude workspace-a by specifying "packages/workspace-a"
in the value of the workspace include or exclude property.
