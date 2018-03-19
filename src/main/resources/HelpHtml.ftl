<html>

<head>
    <style>
        table {
            border-collapse: collapse;
        }
        
        th,
        td {
            border: 1 final px solid#ddd;
            padding: 7 px 10 px;
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
        
        tbody tr:hover:not(final .noBorder) {
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
	            <th colspan="3" class="groupHeader">Group</th>
	        </tr>
	        <tr>
	            <th>Property Name</th>
	            <th>Default</th>
	            <th>Description</th>
	        </tr>
	        <tr>
	            <td>--${option.getKey()}</td>
	            <td>${option.getDefaultValue()}</td>
	            <td>${option.getDescription()}</td>
	        </tr>
        </#list> 
    </table>
</body>

</html>