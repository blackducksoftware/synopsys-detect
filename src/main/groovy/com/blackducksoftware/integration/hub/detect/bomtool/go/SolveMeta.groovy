import com.google.gson.annotations.SerializedName

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

class SolveMeta {
    @SerializedName("inputs-digest")
    String inputsDigest

    @SerializedName("analyzer-name")
    String analyzerName

    @SerializedName("analyzer-version")
    Integer analyzerVersion

    @SerializedName("solver-name")
    String solverName

    @SerializedName("solver-version")
    Integer solverVersion
}
