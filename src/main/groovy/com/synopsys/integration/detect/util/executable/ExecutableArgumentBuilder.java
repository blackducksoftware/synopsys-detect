/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.util.executable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ExecutableArgumentBuilder {
    private final List<Argument> arguments = new ArrayList<>();

    public void addArgumentPair(final String key, final String value, final boolean escape) {
        addArgument(new Pair(key, value, escape));
    }

    public void addArgumentPair(final String key, final String value) {
        addArgumentPair(key, value, false);
    }

    public void insertArgumentPair(final int index, final String key, final String value) {
        insertArgumentPair(index, key, value, false);
    }

    public void insertArgumentPair(final int index, final String key, final String value, final boolean escape) {
        insertArgument(index, new Pair(key, value, escape));
    }

    public void addArgument(final String value) {
        addArgument(value, false);
    }

    public void addArgument(final String value, final boolean escape) {
        addArgument(new StringArgument(value, escape));
    }

    //General argument operations
    public void addArgument(final Argument argument) {
        arguments.add(argument);
    }

    public void insertArgument(final int index, final Argument argument) {
        arguments.add(index, argument);
    }

    public List<String> build() {
        final List<String> outList = new ArrayList<>();
        for (final Argument argument : arguments) {
            outList.add(argument.toArgumentString());
        }
        return outList;
    }

    public String buildString() {
        final List<String> outList = new ArrayList<>();
        for (final Argument argument : arguments) {
            outList.add(argument.toArgumentString());
        }
        return StringUtils.join(outList, " ");
    }

    private interface Argument {
        String toArgumentString();
    }

    private class StringArgument implements Argument {
        public String value;
        public boolean escape;

        public StringArgument(final String value, final boolean escape) {
            this.value = value;
            this.escape = escape;
        }

        @Override
        public String toArgumentString() {
            String escapedValue = value;
            if (escape) {
                escapedValue = "\"" + value + "\"";
            }
            return escapedValue;
        }
    }

    private class Pair implements Argument {
        public String key;
        public String value;
        public boolean escape;

        public Pair(final String key, final String value, final boolean escape) {
            this.key = key;
            this.value = value;
            this.escape = escape;
        }

        @Override
        public String toArgumentString() {
            String escapedValue = value;
            if (escape) {
                escapedValue = "\"" + value + "\"";
            }
            return key + "=" + escapedValue;
        }

    }

}
