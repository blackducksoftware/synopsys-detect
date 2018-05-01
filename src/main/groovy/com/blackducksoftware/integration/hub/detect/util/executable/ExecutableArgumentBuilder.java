package com.blackducksoftware.integration.hub.detect.util.executable;

import java.util.ArrayList;
import java.util.List;

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
