/**
 * buildSrc
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.docs.copied

import java.util.ArrayList

//Copied from detect-configuration
class HelpJsonOption {
    var propertyName = ""
    var propertyKey = ""
    var propertyType = ""
    var defaultValue: String? = ""
    var addedInVersion = ""
    var category = ""
    var group = ""
    var superGroup:String? = ""
    var additionalGroups: List<String> = ArrayList()
    var description = ""
    var detailedDescription = ""
    var deprecated = false
    var deprecatedDescription = ""
    var deprecatedFailInVersion = ""
    var deprecatedRemoveInVersion = ""
    var strictValues = false
    var caseSensitiveValues = false
    var hasAcceptableValues = false
    var isCommaSeparatedList = false
    var acceptableValues: List<String> = ArrayList()

    //This is added for use in the markdown, it does not actually exist on the object.
    var location = ""
}
