<#ftl output_format="Markdown">

# Properties

This page lists all ${solution_name} properties including deprecated and advanced.  For most use cases, refer to [basic properties](basic-properties.md).

<#list groups as group>

[${group.groupName}](${group.location}.md)

| Property | Description |
| --- | --- |
<#list group.simple![] as option>
<#noautoesc>
| [${option.propertyKey}](${option.location}) | <#if option.defaultValue?has_content>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> <#if option.deprecated>**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."} It will be removed in ${option.deprecatedRemoveInVersion}.**</#if> |
</#noautoesc>
</#list>
<#list group.advanced![] as option>
<#noautoesc>
| [${option.propertyKey}](${option.location}) <br /> (Advanced) | <#if option.defaultValue?has_content>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> <#if option.deprecated>**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."} It will be removed in ${option.deprecatedRemoveInVersion}.**</#if> |
</#noautoesc>
</#list>
<#list group.deprecated![] as option>
<#noautoesc>
| [${option.propertyKey}](${option.location}) <br /> (Deprecated)| <#if option.defaultValue?has_content>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> <#if option.deprecated>**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."} It will be removed in ${option.deprecatedRemoveInVersion}.**</#if> |
</#noautoesc>
</#list>

</#list>
