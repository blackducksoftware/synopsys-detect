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
package com.blackducksoftware.integration.hub.detect;

import java.lang.reflect.Modifier

import org.springframework.beans.factory.annotation.Value

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
public class DetectConfigurationPrinter {

    public void printConfiguration(final PrintStream printStream, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions) {
        printStream.println('')
        printStream.println("Detect Version: ${detectConfiguration.buildInfo.detectVersion}" as String)
        printStream.println('Current property values:')
        printStream.println('-'.multiply(60))
        def propertyFields = DetectConfiguration.class.getDeclaredFields().findAll {
            def foundValueAnnotation = it.annotations.find { annotation ->
                annotation.annotationType() == Value.class
            }
            int modifiers = it.modifiers
            !Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers) && foundValueAnnotation
        }.sort { a, b ->
            a.name <=> b.name
        }

        propertyFields.each {
            it.accessible = true
            String fieldName = it.name
            Object fieldValue = it.get(detectConfiguration)
            if (it.type.isArray()) {
                fieldValue = (fieldValue as String[]).join(', ')
            }
            if (fieldName && fieldValue && 'metaClass' != fieldName) {
                if (fieldName.toLowerCase().contains('password')) {
                    fieldValue = '*'.multiply((fieldValue as String).length())
                }
                String profile = null;
                for (DetectOption opt : detectOptions){
                    if (opt.fieldName.equals(fieldName)){
                        profile = opt.defaultValue.lastUsedProfile;
                    }
                }
                if (profile != null){
                    printStream.println("${fieldName} = ${fieldValue} (${profile})" as String)
                }else{
                    printStream.println("${fieldName} = ${fieldValue}" as String)
                }
            }
            it.accessible = false
        }
        printStream.println('-'.multiply(60))
        printStream.println('')
    }
}
