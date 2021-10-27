# pnpm support


${solution_name} runs the pnpm detector if it finds a pnpm-lock.yaml file in your project, and parses the file to obtain information on your project's dependencies.

To specify which types of dependencies you want ${solution_name} to report (eg. development dependencies, optional dependencies, etc) use the detect.pnpm.dependency.types property.

The pnpm detector extracts the project's name and version from the package.json file.  If it does not find a package.json file, it will defer to values derived by git, from the project's directory, or defaults.


