# BitBake support

The BitBake detector generates one codelocation for each given package name by performing the following steps:

1. Runs "bitbake -g" to validate the given package name
1. Runs "bitbake-layers show-recipes" to get details on recipes
1. Generates the graph for the codelocation from the graph read from task-depends.dot plus the recipe details