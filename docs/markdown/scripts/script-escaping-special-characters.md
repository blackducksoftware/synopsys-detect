# Quoting and escaping shell script arguments

## Running the Bash script ([bash_script_name]) on Linux or Mac

The recommended environment for running [bash_script_name] on Linux or Mac is Bash.

When an argument contains a space, you should wrap the argument with escaped quotes.

For example:
```
detect7.sh --detect.project.name=\"Project Test\"
```
````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) --detect.project.name=\"Project Test\"
````

When an argument contains a special character (for example, an exclamation point) you must
escape the character with a backslash. The backslash should precede the escaped character.

For example:
```
detect7.sh --detect.project.name=Project\\!Test
```

## Running in Command Prompt (cmd) on Windows ([powershell_script_name])

The recommended environment for running [powershell_script_name] on Windows is the [Windows Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe).

When an argument contains a space, comma or other special character, you should escape the character with a back quote. The back quote should precede the escaped character.

For example:
```
detect7.ps1 --detect.project.name=Project` Test
```
```
detect7.ps1 --detect.signature.scanner.paths=path1`,path2
```
```
Powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect" --detect.project.name=Project` Test
```

## Running in PowerShell on Windows ([powershell_script_name])

An alternative environment for running [powershell_script_name] on Windows is to run it from inside a PowerShell session.

This invocation has an important distinction from the Command Prompt invocation in that the script does NOT EXIT the session. This is desirable when running the script from a terminal session but not from within a CI/CD environment.

_When running from within a CI/CD environment either omit the passthrough flag or use the command prompt invocation as the job may not set the proper exit code if the session does not exit._

When an argument contains a space, comma or other special character you should wrap the argument in quotes. The quotes can surround either the value or the entire argument. 

For example:
```
detect7.ps1 "--detect.project.name=Project Test"
```
```
detect7.ps1 --detect.tools="DETECTOR,SIGNATURE_SCAN"
```
```
[Net.ServicePointManager]::SecurityProtocol = 'tls12'; $Env:DETECT_EXIT_CODE_PASSTHRU=1; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect --detect.tools="DETECTOR,SIGNATURE_SCAN"
``` 