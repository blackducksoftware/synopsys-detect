# Git project support

Unlike most detectors, the Git detector does not discover dependencies. It only discovers project and project version names.
Ideally the Git detector runs in combination with a package manager detector that discovers dependencies.
If the package manager detector is unable to derive project and project version names,
the Git detector may be able to provide them.

The Git detector will run if it finds a .git subdirectory in your source directory.

If it finds a git executable
(see the [detect git executable](../properties/configuration/paths.md#git-executable)
property)
the Git detector will run git commands and derive the project (repository) and version (branch) from the output. 
Otherwise it will attempt to parse (and derive project and version name information from)
*config*, *HEAD*, and *ORIGIN_HEAD* files within the .git subdirectory.

The Git Parse detector will only be able to discover (and supply to [blackduck_product_name]) the
git commit hash if it finds an *ORIGIN_HEAD* file in the .git directory.
