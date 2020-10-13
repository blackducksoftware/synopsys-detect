#
# Copyright (c) 2020 Synopsys, Inc.
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.


# Uncomment for debugging. Can't use localhost. https://www.jetbrains.com/help/pycharm/remote-debugging-with-product.html#remote-debug-config
# import pydevd_pycharm
# pydevd_pycharm.settrace('<Host IP Address>', port=5002, stdoutToServer=True, stderrToServer=True)

import distutils.dist
import getopt
import io
import os
import pkg_resources
import re
import sys


class PipInspector:
    def __init__(self, project_name=None, requirements_path=None):
        if not os.path.exists(requirements_path):
            raise FileNotFoundError(
                "The requirements file %s does not exist." % requirements_path
            )

        self.requirements_path = requirements_path
        self.project = None
        self.git_pkg_metadata = None

        if project_name is not None:
            self.project = resolve_package_by_name(project_name, [])

        if self.project is None:
            self.project = DependencyNode()
            self.project.name = "n?"
            self.project.version = "v?"

    def _build_git_pkg_metadata(self):
        if self.git_pkg_metadata is not None:
            return
        self.git_pkg_metadata = {}
        for packages in pkg_resources.working_set.entry_keys.values():
            for pkg in packages:
                distribution = pkg_resources.get_distribution(pkg)
                metadata_str = distribution.get_metadata(distribution.PKG_INFO)
                metadata_obj = distutils.dist.DistributionMetadata()
                metadata_obj.read_pkg_file(io.StringIO(metadata_str))
                if not metadata_obj.url.endswith(".git"):
                    continue
                self.git_pkg_metadata[pkg] = metadata_obj.url

    def resolve_git_package(self, requirement):
        self._build_git_pkg_metadata()
        for key in self.git_pkg_metadata:
            if self.git_pkg_metadata[key] in requirement:
                return resolve_package_by_name(key, [])
        return None

    def inspect(self):
        if self.requirements_path is None:
            print(self.project.render())
            return

        requirements = []
        with open(self.requirements_path) as reqs_file:
            for req in reqs_file.readlines():
                if req.strip():
                    requirements.append(req.strip())

        for package_name in requirements:
            if package_name.startswith("git+"):
                requirement = self.resolve_git_package(package_name)
            else:
                package_name = re.split("==|>=|<=|>|<", package_name)[0]
                requirement = resolve_package_by_name(package_name, [])

            if requirement is None:
                print("--" + package_name)
                continue

            self.project.children = self.project.children + [requirement]

        print(self.project.render())


class DependencyNode(object):
    name = None
    version = None
    children = []

    def render(self, layer=1):
        result = self.name + "==" + self.version
        for child in self.children:
            result += "\n" + (" " * 4 * layer)
            result += child.render(layer + 1)
        return result


def get_package_by_name(package_name):
    try:
        return pkg_resources.working_set.by_key[package_name]
    except KeyError:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.lower()]
    except KeyError:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.replace("-", "_")]
    except KeyError:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.replace("_", "-")]
    except KeyError:
        pass
    return None


# Returns a DependencyNode
def resolve_package_by_name(package_name, history):
    node = DependencyNode()
    package = get_package_by_name(package_name)
    if package is None:
        return None
    node.name = package.project_name
    node.version = package.version
    if package_name.lower() not in history:
        for req in package.requires():
            child_node = resolve_package_by_name(
                req.key, history + [package_name.lower()]
            )
            if child_node is not None:
                node.children = node.children + [child_node]
    return node


def main():
    try:
        opts, _ = getopt.getopt(sys.argv[1:], "p:r", ["projectname=", "requirements="])
    except getopt.GetoptError as error:
        print(str(error))
        print(
            "pip-inspector.py -projectname=<project_name>"
            " -requirements=<requirements_path>"
        )
        sys.exit(2)

    project_name = None
    requirements_path = None

    for opt, arg in opts:
        if opt in "--projectname":
            project_name = arg
        elif opt in "--requirements":
            requirements_path = arg

    try:
        inspector = PipInspector(project_name, requirements_path)
    except FileNotFoundError:
        print("r?" + requirements_path)

    inspector.inspect()


if __name__ == "__main__":
    main()
