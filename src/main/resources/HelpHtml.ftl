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
        <#list options as option>
        		<tr class="noBorder">
            		<td colspan="3" class="noBorder"></td>
	        </tr>
	        <tr>
	            <th colspan="3" class="groupHeader">${option.groupName}</th>
	        </tr>
	        <tr>
	            <th>Property Name</th>
	            <th>Default</th>
	            <th>Description</th>
	        </tr>
	        <#list option.detectOptions as detectOption>
		        <tr>
		            <td>--${detectOption.getKey()}</td>
		            <td>${detectOption.getDefaultValue()}</td>
		            <td>${detectOption.getDescription()}</td>
		        </tr>
	        </#list> 
        </#list> 
    </table>
</body>

</html>