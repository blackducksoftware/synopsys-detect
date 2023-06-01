# User role requirements when running with [blackduck_product_name]

Any user can download [solution_name] and run a scan, however you must configure a user/API token in [blackduck_product_name] for the [solution_name] scan to be analyzed by [blackduck_product_name].  
 
For more information on creating a [blackduck_product_name] user token, please consult the documentation provided by [blackduck_product_name] under the topic:
<xref href="MyAccessTokens.dita" scope="peer"> Managing user access tokens.
<data name="facets" value="pubname=bd-hub"/></xref>   
   
   
**The following user roles are required for the user that you create in [blackduck_product_name]**     

* The user must have the Project Creator overall role in order to create [blackduck_product_name] projects.
* The user must have the Global Project Viewer overall role, or be a member of the project, in order to create [blackduck_product_name] project versions.
* The user must have the Project Code Scanner project role, or the Global Code Scanner overall role, in order to populate the project BOM.
