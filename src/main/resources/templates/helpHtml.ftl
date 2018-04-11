<html>

<head>
    <style>
        table {
            border-collapse: collapse;
        }
        
        th,
        td {
            border: 1px solid #ddd;
            padding: 7px 10px;
        }
        
        th.groupHeader {
            font-weight: bold;
            background-color: #ddd;
            text-align: left;
        }
        
        th {
            font-weight: normal;
            background-color: #eee;
            text-align: left;
        }
        
        tbody tr:hover:not(.noBorder) {
            background-color: #f8f8f8;
        }
        
        .propertyColumn {
            width: 400px;
        }
        
        .defaultColumn {
            width: 300px;
        }
        
        .noBorder {
            border: 0px;
        }
    </style>
</head>

<body>
    <table>
        <colgroup>
            <col class="propertyColumn" />
            <col class="defaultColumn" />
            <col />
        </colgroup>
        <#list groups as group>
        		<tr class="noBorder">
            		<td colspan="3" class="noBorder"></td>
	        </tr>
	        <tr>
	            <th colspan="3" class="groupHeader">${group.groupName}</th>
	        </tr>
	        <tr>
	            <th>Property Name</th>
	            <th>Default</th>
	            <th>Description</th>
	        </tr>
	        <#list group.options as option>
		        <tr>
		            <td>--${option.key}</td>
		            <td>${option.defaultValue}</td>
		            <td>${option.description}</td>
		        </tr>
	        </#list> 
        </#list> 
    </table>
</body>

</html>