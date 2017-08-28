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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

import groovy.transform.ToString

@ToString(includePackage=false, includeFields=true, ignoreNulls=true)
@JsonIgnoreProperties(ignoreUnknown = true)
class PodSource {
    @JsonIgnore
    String name

    @JsonProperty(':git')
    String git

    @JsonProperty(':tag')
    String tag

    @JsonProperty(':commit')
    String commit

    @JsonProperty(':path')
    String path
}
