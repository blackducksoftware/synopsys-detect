# Conda Support

${solution_name} runs the Conda detector if an environment.yml file is found in your project.

The Conda detector discovers dependencies of python projects.

The Conda detector requires that the *conda* executable is on the PATH, or that its path is passed in via `--detect.conda.path`.

The Conda detector runs `conda list -n [environment_name] --json` and `conda info --json`, and parses the output of both commands to discover dependencies.
Note: To specify a Conda environment to be referenced when running `conda list`, pass the name of the environment using `--detect.conda.environment.name`.
Refer to [Properties](../../../properties/detectors/conda/) for details.