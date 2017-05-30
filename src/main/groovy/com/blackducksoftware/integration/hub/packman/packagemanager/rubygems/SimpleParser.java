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
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class SimpleParser {
    private final String indentation;

    private final String objectIdenetifier;

    public SimpleParser(final String indentation, final String objectIdentifier) {
        this.indentation = indentation;
        this.objectIdenetifier = objectIdentifier;
    }

    public Map parse(final String gemlockText) {
        final Map map = new HashMap();

        final Stack<Map> mapStack = new Stack<>();

        int level = 0;
        for (final String line : gemlockText.split("\n")) {
            final int currentLevel = getCurrentLevel(line);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(line)) {
                final Entry<String, Map> entry = lineToEntry(line);
                if (currentLevel == 0) {
                    for (; level > currentLevel; level--) {
                        mapStack.pop();
                    }
                    map.put(entry.getKey(), entry.getValue());
                    mapStack.push(entry.getValue());
                    level = currentLevel;
                } else if (currentLevel > level) {
                    mapStack.peek().put(entry.getKey(), entry.getValue());
                    mapStack.push(entry.getValue());
                    level = currentLevel;
                } else if (currentLevel == level) {
                    mapStack.pop();
                    mapStack.peek().put(entry.getKey(), entry.getValue());
                    mapStack.push(entry.getValue());
                } else {
                    for (int i = level; i >= currentLevel; i--) {
                        mapStack.pop();
                    }
                    mapStack.peek().put(entry.getKey(), entry.getValue());
                    mapStack.push(entry.getValue());
                    level = currentLevel;
                }
            }
        }
        return map;
    }

    public Map.Entry<String, Map> lineToEntry(final String line) {
        Map.Entry<String, Map> entry = null;
        if (line.contains(objectIdenetifier)) {
            final String[] lineSegments = line.split(objectIdenetifier);
            final String key = lineSegments[0].trim();
            final Map subMap = new HashMap();
            if (lineSegments.length > 1) {
                final String value = line.replace(key + objectIdenetifier, "");
                subMap.put(value.trim(), null);
            }
            entry = new AbstractMap.SimpleEntry<>(key, subMap);
        } else {
            final String key = line.trim();
            final Map subMap = new HashMap();
            entry = new AbstractMap.SimpleEntry<>(key, subMap);
        }
        return entry;
    }

    public int getCurrentLevel(String line) {
        int level = 0;
        if (indentation != null) {
            while (line.startsWith(indentation)) {
                level++;
                line = line.replaceFirst(indentation, "");
            }
        }
        return level;
    }
}
