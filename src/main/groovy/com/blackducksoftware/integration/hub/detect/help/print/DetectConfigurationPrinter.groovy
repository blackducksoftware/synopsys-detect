/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.help.print;

import java.lang.reflect.Modifier

import org.springframework.beans.factory.annotation.Value

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.help.DetectOption
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingOption
public class DetectConfigurationPrinter {

    public void printDetailedConfiguration(final PrintStream printStream, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions, List<OnboardingOption> onboardedOptions) {
        printStream.println('')
        printStream.println('Current property values:')
        printStream.println('--property = value (profile) [notes]')
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
            Object rawFieldValue = it.get(detectConfiguration)
            String fieldValue
            if (it.type.isArray()) {
                fieldValue = (rawFieldValue as String[]).join(', ')
            }else{
                fieldValue = rawFieldValue.toString()
            }
            if (fieldName && fieldValue && 'metaClass' != fieldName) {
                boolean containsPassword =  fieldName.toLowerCase().contains('password');
                if (containsPassword) {
                    fieldValue = '*'.multiply((fieldValue as String).length())
                }
                DetectOption option;
                for (DetectOption opt : detectOptions){
                    if (opt.fieldName.equals(fieldName)){
                        option = opt;
                    }
                }
                if (option.defaultValue.chosenProfile != null && fieldValue.equals(option.defaultValue.chosenDefault)){
                    printStream.println("${fieldName} = ${fieldValue} (${option.defaultValue.chosenProfile})" as String)
                }else if (option != null && !option.finalValue.equals(fieldValue) && !containsPassword){

                    if (didOnboardField(fieldName, onboardedOptions)){
                        printStream.println("${fieldName} = ${fieldValue} [onboarded]" as String)
                    }else if (option.finalValue.equals("latest")){
                        printStream.println("${fieldName} = ${fieldValue} [latest]" as String)
                    }else if (option.finalValue.trim().size() == 0){
                        printStream.println("${fieldName} = ${fieldValue} [calculated]" as String)
                    }else{
                        printStream.println("${fieldName} = ${fieldValue} [${option.finalValue}]" as String)
                    }
                }else{
                    printStream.println("${fieldName} = ${fieldValue}" as String)
                }
            }
            it.accessible = false
        }
        printStream.println('-'.multiply(60))
        printStream.println('')
    }

    private boolean didOnboardField(String field, List<OnboardingOption> onboardedOptions) {
        for (OnboardingOption option : onboardedOptions){
            if (option.fieldName.equals(field)){
                return true;
            }
        }
        return false;
    }

    public void printOriginalConfiguration(final PrintStream printStream, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions, List<OnboardingOption> onboardedOptions) {
        printStream.println('')
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
            Object rawFieldValue = it.get(detectConfiguration)
            String fieldValue
            if (it.type.isArray()) {
                fieldValue = (rawFieldValue as String[]).join(', ')
            }else{
                fieldValue = rawFieldValue.toString()
            }
            if (fieldName && fieldValue && 'metaClass' != fieldName) {
                if (fieldName.toLowerCase().contains('password')) {
                    fieldValue = '*'.multiply((fieldValue as String).length())
                }
                DetectOption option
                for (DetectOption opt : detectOptions){
                    if (opt.fieldName.equals(fieldName)){
                        option = opt
                    }
                }
                if (option != null && !option.finalValue.equals(fieldValue) && option.finalValue.equals("latest")){
                    printStream.println("${fieldName} = latest (${fieldValue})" as String)
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
