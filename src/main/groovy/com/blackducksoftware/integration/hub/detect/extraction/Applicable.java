package com.blackducksoftware.integration.hub.detect.extraction;

public class Applicable {

    public boolean applied = false;
    public String description = "";

    public Applicable(final boolean applied, final String description) {

    }

    public static Applicable doesNotApply(final String reason){
        return new Applicable(false, reason);
    }

    public static Applicable doesApply(){
        return new Applicable(true, null);
    }
}
