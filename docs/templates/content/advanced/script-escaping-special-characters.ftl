# Quoting shell script arguments

When passing an argument that must be quoted (for example, because it contains a space) to either ${solution_name} shell script (Bash or PowerShell), you need to
quote the argument using escaped quote characters, by preceding each quote with a backslash character.

Bash script example:

    bash <(curl -s -L detect.synopsys.com/detect.sh) --detect.project.name=\"Project Test\"

PowerShell script example:

    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect" --detect.project.name=\"Project Test\"