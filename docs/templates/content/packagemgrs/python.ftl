# Python support

${solution_name} has three detectors for Python:

* [Pip detector](#pipdetector)
* [Pipenv detector](#pipenvdetector)
* [Poetry detector](#poetrydetector)

<a name="pipdetector"></a>
## The Pip detector

The Pip detector discovers dependencies of Python projects.

The Pip detector attempts to run on your project if any of the following are true: a setup.py file is found, a requirements.txt is found, or a requirements file is provided using the [--detect.pip.requirements.path](../../../properties/detectors/pip/#pip-requirements-path) property.

The Pip detector requires Python and pip executables:

* ${solution_name} looks for python (or python3 if the python3 property is set to true) on $PATH. You can override this by setting [--detect.python.python.path](../../../properties/detectors/pip/#python-executable)
* ${solution_name} looks for pip (or pip3 if the python3 property is set to true) on $PATH. You can override this by setting [--detect.pip.path](../../../properties/detectors/pip/#pip-executable)

The Pip detector runs the [pip-inspector.py script](https://github.com/blackducksoftware/synopsys-detect/blob/master/src/main/resources/pip-inspector.py), which uses Python/pip libraries to query the pip cache for the project, which may or may not be a virtual environment, for dependency information:

1. pip-inspector.py queries for the project dependencies by project name which can be discovered using setup.py, or provided using the detect.pip.project.name property, using the [pkg_resources library](https://setuptools.readthedocs.io/en/latest/pkg_resources.html). If your project is installed into the pip cache, this discovers dependencies specified in setup.py.
1. If one or more requirements files are found or provided, pip-inspector.py uses the Python API called parse_requirements to query each requirements file for possible additional dependencies, and uses the pkg_resources library to query for the details of each.

Ramifications of this approach:

* Because pip-inspector.py uses the pkg_resources library to discover dependencies, only those packages which have been installed; using, for example, `pip install`, into the pip cache; in other words, appearing in the output of `pip list`, are included in the output. There must be a match between the package version on which your project depends and the package version installed in the pip cache. Additional details are available in the [pkg_resources library documentation](https://setuptools.readthedocs.io/en/latest/pkg_resources.html).
* If the packages are installed into a virtual environment for your project, you must run ${solution_name} from within that virtual environment.

Recommendations:

* Be sure that ${solution_name} is finding the correct Python executable; this can be done by running the logging level at DEBUG and then reading the log. This is a particular concern if your system has multiple versions of Python installed; you must be sure ${solution_name} is using the correct Python version.
* Create a setup.py file for your project.
* Install your project and dependencies into the pip cache:
````
python setup.py install
pip install -r requirements.txt
````
* The Pip detector derives your project name using your setup.py file if you have one. If you do not have a setup.py file, you must provide the correct project name using the propety --detect.pip.project.name.
* If there are any dependencies specified in requirements.txt that are not specified in setup.py, then provide the requirements.txt file using the ${solution_name} property.
* If you are using a virtual environment, be sure to switch to that virtual environment when you run ${solution_name}. This also applies when you are using a tool such as Poetry that sets up a Python virtual environment.

<a name="pipenvdetector"></a>
## Pipenv detector

The Pipenv detector discovers dependencies of Python projects.

The Pipenv detector attempts to run on your project if either of the following is true:

1. A Pipfile is found.
1. A Pipfile.lock file is found.

The Pipenv detector also requires Python and Pipenv executables:

* ${solution_name} looks for python (or python3 if the python3 property is set to true) on $PATH. You can override this by setting the python path property.
* ${solution_name} looks for pipenv on $PATH.

The Pipenv detector runs `pipenv run pip freeze` and `pipenv graph --bare --json-tree` and derives dependency information from the output. The dependency hierarchy is derived from the output of `pipenv graph --bare --json-tree`. The output of `pipenv run pip freeze` is used to improve the accuracy of dependency versions.

To troubleshoot of the Pipenv detector, start by running `pipenv graph --bare --json-tree`, and making sure that the output looks correct since this is the basis from which ${solution_name} constructs the BDIO. If the output of `pipenv graph --bare --json-tree` does not look correct, make sure the packages (dependencies) are installed into the Pipenv virtual environment (`pipenv install`).

<a name="poetrydetector"></a>
## Poetry detector

The Poetry detector discovers dependencies of Python projects.

The Poetry detector attempts to run on your project if either of the following is true:

1. A poetry.lock file is found.
2. A pyproject.toml file containing a tool.poetry section is found.

The Poetry detector parses poetry.lock for dependency information. If the detector discovers a pyproject.toml file but not a poetry.lock file, it will prompt the user to generate a poetry.lock by running `poetry install` and then run Detect again.
The Poetry detector extracts the project's name and version from the pyproject.toml file.  If it does not find a pyproject.toml file, it will defer to values derived by git, from the project's directory, or defaults.