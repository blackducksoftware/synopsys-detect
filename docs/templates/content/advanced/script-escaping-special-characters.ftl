# Quoting and escaping shell script arguments

## Bash script (${bash_script_name})

When passing an argument that must be quoted (because, for example, it contains a space) to ${bash_script_name}, you need to
quote the argument using escaped quote characters, by preceding each quote with a backslash character. For example:

    bash <(curl -s -L detect.synopsys.com/detect.sh) --detect.project.name=\"Project Test\"

When passing an argument that contains a character that must be escaped (for example, a exclamation point) to ${bash_script_name}, you need to
escape the character by preceding the character with a backslash character and quote the string that contains it. For example:

    bash <(curl -s -L detect.synopsys.com/detect.sh) --detect.project.name="Project\!Test"

## PowerShell script (${powershell_script_name})

When passing an argument that must be quoted (because, for example, it contains a space) to ${powershell_script_name}, you need to
quote the argument using escaped quote characters, by preceding each quote with a back quote character. For example:

    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect" --detect.project.name=`"Project Test`"

When passing an argument that contains a character that must be escaped (for example, a back quote character) to ${powershell_script_name}, you need to
escape the character by preceding the character with a back quote character. For example:

    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect" --detect.project.name=Project``Test

