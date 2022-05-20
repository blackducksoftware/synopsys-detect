# Git project support

Unlike most detectors, the git detector does not discover dependencies. It only discovers project and project version names.
Ideally the git detector runs in combination with a package manager detector that discovers dependencies.
If the package manager detector is unable to derive project and project version names,
the git detector may be able to provide them.

The git detector will run if it finds a .git subdirectory in your source directory.

If it finds a git executable
(see the [detect git executable](../properties/configuration/paths.md#git-executable)
property)
the git detector will run git commands and derive the project (repository) and version (branch) from the output. 
Otherwise it will attempt to parse (and derive project and version name information from)
*config* and *HEAD* files within the .git subdirectory.
