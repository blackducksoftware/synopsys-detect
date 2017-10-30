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
package com.blackducksoftware.integration.hub.detect.help;

import java.lang.reflect.Field;

import org.apache.commons.lang3.math.NumberUtils


public class ReflectionUtils {

    public static void setValue(final Field field, final Object obj, final String value, final boolean force){
        final Class type = field.getType()
        Object fieldValue = field.get(obj)
        if (String.class == type && (force || !(fieldValue as String)?.trim())) {
            field.set(obj, value)
        } else if (Integer.class == type && (force || fieldValue == null)) {
            field.set(obj, NumberUtils.toInt(value))
        } else if (Long.class == type && (force || fieldValue == null)) {
            field.set(obj, NumberUtils.toLong(value))
        } else if (Boolean.class == type && (force || fieldValue == null)) {
            field.set(obj, Boolean.parseBoolean(value))
        }
    }
}
