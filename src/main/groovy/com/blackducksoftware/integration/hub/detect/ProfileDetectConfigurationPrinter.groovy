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
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingOption
public class ProfileDetectConfigurationPrinter implements DetectConfigurationPrinter{

    public void printHeader(final PrintStream printStream, DetectInfo detectInfo, final List<DetectOption> detectOptions) {
        printStream.println('')
        printStream.println("Detect Version: ${detectInfo.detectVersion}" as String)
        printStream.println('')
    }

    public void printConfiguration(final PrintStream printStream, DetectInfo detectInfo, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions, List<OnboardingOption> onboardedOptions) {
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
                DetectOption option;
                String profile = null;
                for (DetectOption opt : detectOptions){
                    if (opt.fieldName.equals(fieldName)){
                        profile = opt.defaultValue.chosenProfile;
                        option = opt;
                    }
                }
                if (profile != null){
                    printStream.println("${fieldName} = ${fieldValue} (${profile})" as String)
                }else if (option != null && !option.finalValue.equals(fieldValue)){

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
}
