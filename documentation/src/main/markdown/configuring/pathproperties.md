# Path properties

Each [detect_product_short] property with a type of "Path" or "Optional Path" accepts a file path value.
(These properties also tend to have names that end with ".path".)
The file path value can be either absolute (e.g. /usr/bin/conan) or relative to the directory
from which [detect_product_short] is executed (e.g. ../bin/conan).
The format of the file path (the directory separator character, whether or not a drive letter prefix is supported, etc.)
is dictated by the operating system on which [detect_product_short] is running.

Path property value examples (applicable when executing the [detect_product_short] .jar directly):

* Linux/Mac: --detect.conan.path="/usr/bin/conan"
* Windows: --detect.npm.path="C:\Program Files\nodejs\npm.cmd"

When running [detect_product_short] using one of the scripts, remember to also apply quoting and escaping rules that
apply. For more information refer to [Quoting and escaping shell script arguments](../scripts/script-escaping-special-characters.md).
