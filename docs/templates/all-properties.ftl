<#list groups as group>

${group.groupName}

| Propery Name | Default | Acceptable Values | Description | Detailed Description | Deprecation |
| --- | --- | --- | --- | --- | --- | 
<#list group.options as option>
| ${option.propertyKey} | ${option.defaultValue!""} | ${option.acceptableValues?join(", ")} | ${option.description} | ${option.detailedDescription} | ${option.deprecatedDescription!""} |
</#list> 

</#list> 