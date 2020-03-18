# Yarn support

${solution_name} runs the Yarn detector if it finds both of the following files in your project:

* yarn.lock
* package.json

The Yarn detector reads both files and derives dependency information from their contents.

## Yarn Workspace Support

${solution_name} does not currently support Yarn workspaces as defined here [https://classic.yarnpkg.com/en/docs/workspaces/](https://classic.yarnpkg.com/en/docs/workspaces/).