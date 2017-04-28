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
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.parsers;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.GsonBuilder;

public class GemlockParser {

    public List<DependencyNode> parse(final String gemlockText) {

        final YamlReader reader = new YamlReader(gemlockText);
        Object object;
        try {
            object = reader.read();
        } catch (final YamlException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

        if (object != null) {
            System.out.println(object);
            final Map map = (Map) object;
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(map));
        }

        return null;
    }
}
