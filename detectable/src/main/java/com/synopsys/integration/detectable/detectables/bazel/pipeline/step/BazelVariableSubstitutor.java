/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BazelVariableSubstitutor {
    private final Map<String, String> stringSubstitutions;
    private final Map<String, List<String>> listInsertions;

    public BazelVariableSubstitutor(final String bazelTarget, final List<String> cqueryAdditionalOptions) {
        // the keys are regex's, requiring regex special character escaping
        stringSubstitutions = new HashMap<>(1);
        stringSubstitutions.put("\\$\\{detect.bazel.target}", bazelTarget);

        // the keys are plain strings
        listInsertions = new HashMap<>(1);
        listInsertions.put("${detect.bazel.cquery.options}", cqueryAdditionalOptions);
    }

    public List<String> substitute(final List<String> origStrings, final String input) {
        final List<String> modifiedStrings = new ArrayList<>(origStrings.size());
        for (String origString : origStrings) {
            boolean foundListInsertionPlaceholder = handleListInsertion(modifiedStrings, origString, input);
            if (!foundListInsertionPlaceholder) {
                handleStringSubstitutions(input, modifiedStrings, origString);
            }
        }
        return modifiedStrings;
    }

    private void handleStringSubstitutions(final String input, final List<String> modifiedStrings, final String origString) {
        modifiedStrings.add(substitute(origString, input));
    }

    private boolean handleListInsertion(final List<String> modifiedStrings, final String origString, final String input) {
        boolean foundListInsertionVariable = false;
        for (final Map.Entry<String, List<String>> listInsertion : listInsertions.entrySet()) {
            if (origString.equals(listInsertion.getKey())) {
                foundListInsertionVariable = true;
                final List<String> valuesToInsert = listInsertion.getValue();
                if (valuesToInsert != null) {
                    for (final String valueToInsert : valuesToInsert) {
                        // Substituting here gives user the ability to use ${detect.bazel.target} and ${input.item} in list insertion values
                        handleStringSubstitutions(input, modifiedStrings, valueToInsert);
                    }
                }
                break; // it'll only match one
            }
        }
        return foundListInsertionVariable;
    }

    private String substitute(final String origString, final String input) {
        String modifiedString = origString;
        if (input != null) {
            stringSubstitutions.put("\\$\\{input.item}", input);
        }
        for (final Map.Entry<String, String> substitution : stringSubstitutions.entrySet()) {
            modifiedString = modifiedString.replaceAll(substitution.getKey(), substitution.getValue());
        }
        return modifiedString;
    }
}
