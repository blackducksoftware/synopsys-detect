/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import com.google.gson.annotations.SerializedName

import groovy.transform.TypeChecked

@TypeChecked
class NpmDependency {
    @SerializedName('version')
    String version

    @SerializedName('requires')
    Map<String, String> requires
}
