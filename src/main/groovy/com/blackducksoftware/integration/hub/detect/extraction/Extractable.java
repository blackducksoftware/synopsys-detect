package com.blackducksoftware.integration.hub.detect.extraction;

public class Extractable {

    public boolean capable = false;
    public String description = "";

    public Extractable(final boolean capable, final String description) {

    }

    public static Extractable canNotExtract(final String reason){
        return new Extractable(false, reason);
    }

    public static Extractable canExtract(){
        return new Extractable(true, null);
    }
}
