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
#

# Uncomment for debugging. Can't use localhost. https://www.jetbrains.com/help/pycharm/remote-debugging-with-product.html#remote-debug-config
# import pydevd_pycharm
# pydevd_pycharm.settrace('<Host IP Address>', port=5002, stdoutToServer=True, stderrToServer=True)

import getopt
import os
import pip
import pkg_resources
import sys

pip_major_version = int(pip.__version__.split(".")[0])
if pip_major_version >= 20:
    from pip._internal.req import parse_requirements
    from pip._internal.network.session import PipSession
elif pip_major_version >= 10:
    from pip._internal.req import parse_requirements
    from pip._internal.download import PipSession
else:
    from pip.req import parse_requirements
    from pip.download import PipSession

def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'p:r', ['projectname=', 'requirements='])
    except getopt.GetoptError as error:
        print(str(error))
        print('integration-pip-inspector.py -projectname=<project_name> -requirements=<requirements_path>')
        sys.exit(2)

    project_name = None
    requirements_path = None

    for opt, arg in opts:
        if opt in '--projectname':
            project_name = arg
        elif opt in '--requirements':
            requirements_path = arg

    project = None

    if project_name is not None:
        project = resolve_package_by_name(project_name, [])

    if project is None:
        project = DependencyNode()
        project.name = 'n?'
        project.version = 'v?'

    if requirements_path is not None:
        try:
            assert os.path.exists(requirements_path), ("The requirements file %s does not exist." % requirements_path)
            requirements = parse_requirements(requirements_path, session=PipSession())
            for req in requirements:
                try:
                    package_name = None
                    # In 20.1 of pip, the requirements object changed
                    if hasattr(req, 'req'):
                        package_name = req.req.name
                    if package_name is None:
                        import re
                        package_name = re.split('==|>=|<=|>|<', req.requirement)[0]

                    requirement = resolve_package_by_name(package_name, [])
                    if requirement is None:
                        raise Exception()
                    project.children = project.children + [requirement]
                except:
                    if req is not None and req.req is not None:
                        print('--' + req.req.name)
        except AssertionError:
            print('r?' + requirements_path)
        except:
            print('p?' + requirements_path)

    print(project.render())


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
        # TODO: By using pkg_resources.Requirement.parse to get the correct key, we may not need to attempt the other methods. Robust tests are needed to confirm.
        return pkg_resources.working_set.by_key[pkg_resources.Requirement.parse(package_name).key]
    except:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name]
    except:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.lower()]
    except:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.replace('-', '_')]
    except:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.replace('_', '-')]
    except:
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
            child_node = resolve_package_by_name(req.key, history + [package_name.lower()])
            if child_node is not None:
                node.children = node.children + [child_node]
    return node

main()
