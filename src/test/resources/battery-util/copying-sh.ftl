#!/bin/bash

EXTRACTION_FOLDER_FULL=$${extractionFolderIndex}
EXTRACTION_FOLDER=`echo ${r"${EXTRACTION_FOLDER_FULL}"} | cut -c${extractionFolderPrefix?length + 1}-`
echo ${r"${EXTRACTION_FOLDER}"}
<#list files as file>
    mv ${file.from} "${r"${EXTRACTION_FOLDER}"}/${file.to}"
    echo "Moved ${file.from} to ${r"${EXTRACTION_FOLDER}"}/${file.to}"
</#list>