@echo off

set extractionFolderFull=%${extractionFolderIndex}
set extractionFolder=%extractionFolderFull:~${extractionFolderPrefix?length}%
echo %extractionFolder%
<#list files as file>
move ${file.from} %extractionFolder%/${file.to}
echo "Moved ${file.from} to %extractionFolder%/${file.to}"
</#list>