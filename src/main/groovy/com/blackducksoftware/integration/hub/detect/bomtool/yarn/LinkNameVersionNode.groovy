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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import org.apache.commons.lang3.builder.RecursiveToStringStyle
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode

class LinkNameVersionNode implements NameVersionNode {
    String name
    String version
    List<NameVersionNode> children = []

    NameVersionNode link

    @Override
    String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE)
    }

    //    @Override
    //    public String getName() {
    //        if(link) {
    //            return link.name
    //        }
    //
    //        name
    //    }
    //
    //    @Override
    //    public String getVersion() {
    //        if(link) {
    //            return link.version
    //        }
    //
    //        version
    //    }
    //
    //    @Override
    //    public List<? extends NameVersionNode> getChildren() {
    //        if(link) {
    //            return link.children
    //        }
    //
    //        children
    //    }
    //
    //    @Override
    //    public void setName(String name) {
    //        if(link) {
    //            link.name = name
    //        }
    //
    //        this.name = name
    //    }
    //
    //    @Override
    //    public void setVersion(String version) {
    //        if(link) {
    //            link.version = version
    //        }
    //
    //        this.version = version
    //    }
    //
    //    @Override
    //    public void setChildren(List<? extends NameVersionNode> children) {
    //        if(link) {
    //            link.children = children
    //        }
    //
    //        this.children = children
    //    }
}
