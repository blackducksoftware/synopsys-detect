/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
class HelpJsonDetector {
    var detectableName = ""
    var detectableDescriptiveName = ""
    var detectableGroup = ""
    var detectorType = ""
    var detectorName = ""
    var detectorDescriptiveName = ""
    var maxDepth = 0
    var nestable = false
    var nestInvisible = false

    var yieldsTo: List<String> = ArrayList()
    var fallbackTo = ""
}
