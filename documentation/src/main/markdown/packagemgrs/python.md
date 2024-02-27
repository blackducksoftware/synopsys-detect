# Python support

## Related properties

[Detector properties](../properties/detectors/python.md)

## Overview

[solution_name] detectors for Python:

* Pipenv detector
* Pip detector (Pip Native Inspector)
* Pipfile lock detector
* Pip Requirements File Parser
* Poetry detector

## The Pip detector

The Pip detector attempts to run on your project if any of the following are true: a setup.py file is found, a requirements.txt is found, or a requirements file is provided using the [--detect.pip.requirements.path](../properties/detectors/pip.md#pip-requirements-path) property.

The Pip detector requires Python and pip executables:

* [solution_name] looks for python on $PATH. You can override this by setting [--detect.python.path](../properties/detectors/python.md#python-executable)
* [solution_name] looks for pip on $PATH. You can override this by setting [--detect.pip.path](../properties/detectors/pip.md#pip-executable)

The Pip detector runs the [pip-inspector.py script](https://github.com/blackducksoftware/synopsys-detect/blob/master/src/main/resources/pip-inspector.py), which uses Python/pip libraries to query the pip cache for the project, which may or may not be a virtual environment, for dependency information:

1. pip-inspector.py queries for the project dependencies by project name which can be discovered using setup.py, or provided using the detect.pip.project.name property, using the [pkg_resources library](https://setuptools.readthedocs.io/en/latest/pkg_resources.html). If your project is installed into the pip cache, this discovers dependencies specified in setup.py.
1. If one or more requirements files are found or provided, pip-inspector.py uses the Python API called parse_requirements to query each requirements file for possible additional dependencies, and uses the pkg_resources library to query for the details of each.

<note type="tip">pip-inspector.py uses the pkg_resources library to discover dependencies, only those packages which have been installed; using, for example, `pip install`, into the pip cache and appearing in the output of `pip list`, are included in the output. There must be a match between the package version on which your project depends and the package version installed in the pip cache. Additional details are available in the [pkg_resources library documentation](https://setuptools.readthedocs.io/en/latest/pkg_resources.html).</note>   
<note type="note">If the packages are installed into a virtual environment for your project, you must run [solution_name] from within that virtual environment.</note>

### Recommendations for Pip Detector

* Be sure that [solution_name] is locating the correct verion of the Python executable; this can be done by running the logging level at DEBUG and then reading the log. This is a particular concern if your system has multiple versions of Python installed.
* Create a setup.py file for your project.
* Install your project and dependencies into the pip cache:
````
python setup.py install
pip install -r requirements.txt
````
* The Pip detector derives your project name using your setup.py file if you have one. If you do not have a setup.py file, you must provide the correct project name using the propety --detect.pip.project.name.
* If there are any dependencies specified in requirements.txt that are not specified in setup.py, then provide the requirements.txt file using the [solution_name] property.
* If you are using a virtual environment, be sure to switch to that virtual environment when you run [solution_name]. This also applies when you are using a tool such as Poetry that sets up a Python virtual environment.

## Pipenv detector

The Pipenv detector attempts to run on your project if either a Pipfile or Pipfile.lock file is found.

The Pipenv detector requires Python and Pipenv executables:

* [solution_name] looks for python (or python3 if the python3 property is set to true) on $PATH. You can override this by setting the python path property.
* [solution_name] looks for pipenv on $PATH.

The Pipenv detector runs `pipenv run pip freeze` and `pipenv graph --bare --json-tree` and derives dependency information from the output. The dependency hierarchy is derived from the output of `pipenv graph --bare --json-tree`. The output of `pipenv run pip freeze` is used to improve the accuracy of dependency versions.

To troubleshoot of the Pipenv detector, start by running `pipenv graph --bare --json-tree`, and making sure that the output looks correct since this is the basis from which [solution_name] constructs the BDIO. If the output of `pipenv graph --bare --json-tree` does not look correct, make sure the packages (dependencies) are installed into the Pipenv virtual environment (`pipenv install`).

<note type="note">The [detect.pipfile.dependency.types.excluded](../properties/detectors/pip.md#pipfile-dependency-types-excluded) property does not apply to the Pipenv detector.</note>

## Pipfile lock detector

The Pipfile lock detector attempts to run on your project if either a Pipfile.lock or Pipfile file is found AND neither of the Pip or Pipenv detectors apply:

The Pipfile lock detector parses the Pipfile.lock file for dependency information. If the detector discovers a Pipfile file but not a Pipfile.lock file, it will prompt the user to generate a Pipfile.lock file by running `pipenv lock` and then run [solution_name] again.
Pipfile.lock dependencies can be filtered using the [detect.pipfile.dependency.types.excluded](../properties/detectors/pip.md#pipfile-dependency-types-excluded) property.

## Pip requirements file parser

The Pip Requirements File Parser is a buildless detector that acts as a LOW accuracy fallback for the Pip Native Inspector. This detector gets triggered for Pip projects that contain one or more requirements.txt files but [solution_name] doesn't have access to a Pip executable in the environment where the scan is executed.
 
### Requirements file selection (Default)

In the default case, the detector will search for a file named `requirements.txt` for parsing. If the file has references to other `requirements.txt` files in the project provided via the `-r` or `--requirement` option, the detector will try to resolve these file references and also include them for parsing. 
<note type="note">The options `-r` or `--requirement` expect a UNIX path (relative or absolute) to the other requirements files. If an invalid path is provided via these options, [solution_name] will display a warning and continue parsing the initial requirements file.</note>

### Requirements file selection (Override)

The `--detect.pip.requirements.path` property can be used to provide an explicit comma-separated list of paths to requirements files that the parser should include. These files may be present in directories other than the source directory and may have file names other than the default file name "requirements.txt". Since this property is an override, only the files referenced via this property are considered for parsing, and the detector will not include any other file references in the given files content via the `-r` or `--requirement` option.

### Parsing

This parser is a LOW accuracy, best-effort detector. In most cases, it can extract dependency information from the file when entries are in the format `<dependency_name> == <version_name>`. This is the typical format in requirements files generated with the Pip CLI using the `pip freeze > requirements.txt` command. The parser does not resolve any special wildcard characters in the version string, for example a version string present as `1.2.*` is extracted exactly as it is.
In cases where a range of versions is provided for a dependency, for example `>= 1.2.3, <1.3`, the parser will extract the lowest version in the range i.e. `1.2.3`.
Any extras added after the package name are not resolved by the parser. For example, for a package name declared as `requests[security]`, only the requests package is extracted and not the extra option specified as `[security]`.

## Poetry detector

The Poetry detector attempts to run on your project if either a poetry.lock or pyproject.toml file containing a tool.poetry section is found.

The Poetry detector parses poetry.lock for dependency information. If the detector discovers a pyproject.toml file but not a poetry.lock file, it will prompt the user to generate a poetry.lock by running `poetry install` and then run [solution_name] again.
The Poetry detector extracts the project's name and version from the pyproject.toml file.  If it does not find a pyproject.toml file, it will defer to values derived by git, from the project's directory, or defaults.
