# Path properties

Each ${solution_name} property with a type of "Path" or "Optional Path" accepts a file path value.
(These properties also tend to have names that end with ".path".)
The file path value can be either absolute (e.g. /usr/bin/conan) or relative to the directory
from which ${solution_name} is executed (e.g. ../bin/conan).
The format of the file path (the directory separator character, whether or not a drive letter prefix is supported, etc.)
is dictated by the operating system on which ${solution_name} is running.

Path property value examples:

* Linux/Mac: --detect.conan.path="/usr/bin/conan"
* Windows: --detect.npm.path="C:\Program Files\nodejs\npm.cmd"


