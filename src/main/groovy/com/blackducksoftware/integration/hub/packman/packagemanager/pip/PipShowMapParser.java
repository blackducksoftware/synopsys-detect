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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class PipShowMapParser {
    public Map<String, String> parse(final String pipShowText) {
        final Map<String, String> map = new HashMap<>();
        for (final String line : pipShowText.split("\n")) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(line)) {
                final Entry<String, String> entry = lineToEntry(line);
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public Entry<String, String> lineToEntry(final String line) {
        Entry<String, String> entry = null;
        final String objectIdenetifier = ":";
        if (line.contains(objectIdenetifier)) {
            final List<String> lineSegments = new ArrayList<>(Arrays.asList(line.split(objectIdenetifier)));
            final String key = lineSegments.remove(0).trim();
            final String value = StringUtils.join(lineSegments, objectIdenetifier);
            entry = new AbstractMap.SimpleEntry<>(key, value);
        } else {
            final String key = line.trim();
            entry = new AbstractMap.SimpleEntry<>(key, null);
        }
        return entry;
    }
}
