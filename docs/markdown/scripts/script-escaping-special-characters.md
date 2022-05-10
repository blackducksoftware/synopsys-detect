# Quoting and escaping shell script arguments

## Running the Bash script ([bash_script_name]) on Linux or Mac

The recommended environment ("parent shell") for running [bash_script_name] on Linux is Bash. On Mac it is Bash or Zsh.
When using version 8 or later of the bash script ([bash_script_name]), follow the parent shell's quoting/escaping rules when passing arguments to [bash_script_name].

For example:
```
# name: Project Test
detect8.sh --detect.project.name="Project Test" 

# name: Project!Test
detect8.sh --detect.project.name=Project\!Test 

# license: BSD 3-clause "New" or "Revised" License
detect8.sh --detect.project.version.license='BSD 3-clause \"New\" or \"Revised\" License' 
```

## Running in Command Prompt (cmd) on Windows ([powershell_script_name])

The recommended environment ("parent shell") for running [powershell_script_name] on Windows is the [Windows Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe).
Follow the parent shell's quoting/escaping rules when passing arguments to [bash_script_name].

For example:
```
detect8.ps1 --detect.project.name=Project` Test
```
```
detect8.ps1 --detect.signature.scanner.paths=path1`,path2
```
```
Powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect" --detect.project.name=Project` Test
```

## Running in PowerShell on Windows ([powershell_script_name])

An alternative environment for running [powershell_script_name] on Windows is to run it from inside a PowerShell session.

This invocation has an important distinction from the Command Prompt invocation in that the script does NOT EXIT the session. This is desirable when running the script from a terminal session but not from within a CI/CD environment.

_When running from within a CI/CD environment either omit the passthrough flag or use the command prompt invocation as the job may not set the proper exit code if the session does not exit._

When an argument contains a space, comma or other special character, you can wrap the argument in quotes. The quotes can surround either the value or the entire argument. 

For example:
```
detect8.ps1 "--detect.project.name=Project Test"
```
```
detect8.ps1 --detect.tools="DETECTOR,SIGNATURE_SCAN"
```
```
[Net.ServicePointManager]::SecurityProtocol = 'tls12'; $Env:DETECT_EXIT_CODE_PASSTHRU=1; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect --detect.tools="DETECTOR,SIGNATURE_SCAN"
``` 
