#!/bin/bash

INDEX=`cat ${dataFile}`

<#list files as file>
    ARRAY[${file?index}]="${file}"
</#list>

cat ${r"${ARRAY[${INDEX}]}"}

NEXT_INDEX=$((INDEX+1))
echo ${r"${NEXT_INDEX}"} > ${dataFile}
