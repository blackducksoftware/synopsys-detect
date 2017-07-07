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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl

@Component
class CpanListParser {
    public Map<String, NameVersionNode> parse(String listText) {
        Map<String, NameVersionNode> moduleMap = [:]

        for (String line: listText.split('\n')) {
            if(!line.trim()) {
                continue
            }

            String[] module = line.split('\t')
            def nameVersionNode = new NameVersionNodeImpl()
            nameVersionNode.name = module[0].trim()
            nameVersionNode.version = module[1].trim()
            moduleMap[nameVersionNode.name] = nameVersionNode
        }

        moduleMap
    }
}
