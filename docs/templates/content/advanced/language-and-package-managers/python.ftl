# Python support

Detect has two detectors for Python:

* [Pip detector](#pipdetector)
* [Pipenv detector](#pipenvdetector)

<a name="pipdetector"></a>
## The Pip detector

The Pip detector can discover dependencies of Python projects.

The Pip detector will attempt to run on your project if either a setup.py file is found, or a requirements.txt file is provided via the --detect.pip.requirements.path property.

The Pip detector also requires python and pip executables:

* Detect looks for python (or python3 if the python3 property is set to true) on $PATH. You can override this by setting the python path property.
* Detect looks for pip (or pip3 if the python3 property is set to true) on $PATH.

The Pip detector runs the [pip-inspector.py script](https://github.com/blackducksoftware/synopsys-detect/blob/master/src/main/resources/pip-inspector.py), which uses python/pip libararies to query the pip cache for the project (which may or may not be a virtual environment) for dependency information:

1. pip-inspector.py queries for the project dependencies by project name (which can be discovered using setup.py, or provided via the detect.pip.project.name property) using the [pkg_resources library](https://setuptools.readthedocs.io/en/latest/pkg_resources.html). If your project has been installed into the pip cache, this will discover dependencies specified in setup.py.
1. If a requirements.txt file was provided, pip-inspector.py uses a python API, parse_requirements, to query the requirements.txt file for possible additional dependencies, and uses the pkg_resources library to query for the details of each one. (The parse_requirements API is unstable, leading to the decision to deprecate this detector.)

Ramifications of this approach:

* Because pip-inspector.py uses the pkg_resources library to discover dependencies, only those packages which have been installed (via, say, `pip install`) into the pip cache (that is, appear in the output of `pip list`) will be included in the output. Naturally, there must be a match between the package version your project depends on and the package version installed in the pip cache. Additional details are available in the [pkg_resources library documentation](https://setuptools.readthedocs.io/en/latest/pkg_resources.html).
* If the packages have been installed into a virtual environment for your project, you will need to run Detect from within that virtual environment.
* If you are using a tool (such as poetry) that sets up a python virtual environment, you will need to run Detect from within that virtual environment.

Recommendations:

* Be sure that Detect is finding the correct python executable (run with logging level at DEBUG and read the log). This is a particular concern if your system has multiple versions of python installed; you want to be sure Detect is using the right python version.
* Create a setup.py file for your project.
* Install your project and dependencies into the pip cache:
````
python setup.py install
pip install -r requirements.txt
````
* The Pip detector will derive your project name using your setup.py file if you have one. If you do not have a setup.py file, you must provide the correct project name using the --detect.pip.project.name property.
* If there are any dependencies specified in requirements.txt that are not specified in setup.py, provide the requirements.txt file via the Detect property.
* If you are using a virtual environment, make sure to switch to that virtual environment when you run Detect. This also applies when you are using a tool (such as poetry) that sets up a python virtual environment.

<a name="pipenvdetector"></a>
## Pipenv detector

The Pipenv detector can discover dependencies of Python projects.

The Pipenv detector will attempt to run on your project if either of the following is true:

1. A Pipfile is found
1. A Pipfile.lock file is found

The Pipenv detector also requires: python and pipenv executables:

* Detect looks for python (or python3 if the python3 property is set to true) on $PATH. You can override this by setting the python path property.
* Detect looks for pipenv on $PATH.

The Pipenv detector runs `pipenv run pip freeze` and `pipenv graph --bare` and derives dependency info from the output. The dependency hierarchy is derived from the output of `pipenv graph --bare`. The output of `pipenv run pip freeze` is used only to improve the accuracy of dependency versions.

To troubleshooting of the Pipenv detector, start by running `pipenv graph --bare`, and making sure that output looks correct (since this is the basis from which Detect constructs the BDIO). If the output of `pipenv graph --bare` does not look correct, make sure the packages (dependencies) have been installed into the pipenv virtual environment (`pipenv install`).
