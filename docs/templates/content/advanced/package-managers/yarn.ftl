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
to build the complete graph of direct and transient dependencies. The Yarn detector
produces a single codelocation with this graph.

${solution_name} supports projects that use Yarn version 1 or version 2.

## Yarn workspace support

${solution_name} supports Yarn projects that use workspaces.
By default, only workspaces that are dependencies of the
root project (directly or indirectly) are included in the produced dependency graph.
The workspace exclude/include filters can be used to force workspaces to be excluded
or included.

## Monorepo support

If your root project contains workspaces but does not depend on any of them, you can
use the workspace include filter to force some or all of the workspaces to be included.

## Using the workspace exclude and include filters

When using the workspace exclude and include filters, specify workspaces by
name (workspace package.json name field value), not by directory path.