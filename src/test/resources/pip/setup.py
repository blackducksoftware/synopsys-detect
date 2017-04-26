import os

from setuptools import setup

setup(
    name="blackduck_sample_project",
    version="0.0.9",
    author="Black Duck Software",
    author_email="",
    description=("A sample project for using the hub-pip"),
    license="Apache 2.0",
    keywords="sample example blackduck hub-pip",
    url="https://github.com/blackducksoftware/hub_pip",
    packages=[],
    install_requires=["Delorean", "pynamodb"],
    long_description="Look at README.md",
    classifiers=[
        "Development Status :: 2 - Pre-Alpha",
        "Topic :: Utilities",
        "License :: OSI Approved :: Apache 2.0 License",
    ],
)
