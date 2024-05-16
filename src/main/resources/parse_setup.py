import ast

def parse_install_requires(setup_file):
    with open(setup_file, 'r') as file:
        content = file.read()

    module = ast.parse(content)

    for node in ast.walk(module):
        if isinstance(node, ast.Call) and hasattr(node.func, 'id') and node.func.id == 'setup':
            for keyword in node.keywords:
                if keyword.arg == 'install_requires':
                    if isinstance(keyword.value, ast.List):
                        return [element.s for element in keyword.value.elts]

    return []