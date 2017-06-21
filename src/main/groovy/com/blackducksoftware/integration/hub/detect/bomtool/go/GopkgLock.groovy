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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import com.google.gson.annotations.SerializedName

class GopkgLock {
    // see https://github.com/golang/dep/blob/master/lock.go for the source of the lock file
    List<Project> projects

    @SerializedName("solve-meta")
    SolveMeta solveMeta
}
