# Deprecated Properties

This page lists ${solution_name}'s deprecated properties; for both active and deprecated properties, refer to [all properties](../all-properties).

<#list groups as group>

## [${group.groupName}](../${group.location})

| Property | Description |
| --- | --- |
<#list group.options as option>
| [${option.propertyKey}](${option.location}) | <#if option.defaultValue?has_content>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> **DEPRECATED: ${option.deprecatedDescription} It will be removed in ${option.deprecatedRemoveInVersion}.** |
</#list>

</#list>
