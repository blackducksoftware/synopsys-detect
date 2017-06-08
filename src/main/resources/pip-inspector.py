import pkg_resources
import os
import sys
import getopt
import pip


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'p:r', ['project_name=', 'requirements='])
    except getopt.GetoptError as error:
        print(str(error))
        print('integration-pip-inspector.py -p <project_name> -r <requirements_path>')
        print('integration-pip-inspector.py -p <project_name> -r <requirements_path>')
        sys.exit(2)

    project_name = None
    requirements_path = None

    for opt, arg in opts:
        if opt in ('-p', '--project'):
            project_name = arg
        elif opt in ('-r', '--requirements'):
            requirements_path = arg

    if project_name is not None:
        project = resolve_package_by_name(project_name)
    else:
        project = DependencyNode()
        project.name = 'n?'
        project.version = 'v?'

    if requirements_path is not None:
        try:
            assert os.path.exists(requirements_path), ("The requirements file %s does not exist." % requirements_path)
            requirements = pip.req.parse_requirements(requirements_path, session=pip.download.PipSession())
            for req in requirements:
                try:
                    requirement = resolve_package_by_name(req.req.name)
                    project.children = project.children + [requirement]
                except:
                    print('--' + req.req.name)
        except:
            print('r?' + requirements_path)

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
        return pkg_resources.working_set.by_key[package_name]
    except:
        pass
    try:
        return pkg_resources.working_set.by_key[package_name.lower()]
    except:
        pass
    return None


# Returns a DependencyNode
def resolve_package_by_name(package_name):
    node = DependencyNode()
    package = get_package_by_name(package_name)
    node.name = package.project_name
    node.version = package.version
    for req in package.requires():
        child_node = resolve_package_by_name(req.key)
        node.children = node.children + [child_node]
    return node


main()
