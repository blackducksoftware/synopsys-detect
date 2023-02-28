package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

public class SignatureScanResult {
    public static final String OUTPUT_FILE_PATH = "/output/scanOutput.json";

    @SerializedName("version")
    private String version;

    @SerializedName("scanId")
    private String scanId;
    
    @SerializedName("exitStatus")
    private String exitStatus;
    
    @SerializedName("scans")
    private Map<String, String> scans;

    public String getVersion() {
        return version;
    }

    public String getScanId() {
        return scanId;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public Map<String, String> getScans() {
        return scans;
    }

    public Set<String> parseScanIds() {
        HashSet<String> ids = new HashSet<>();
        
        // Attempt to get all scans the signature scanner might have invoked
        if (getScans() != null && !getScans().isEmpty()) {
            for (String scanId : getScans().values()) {
                // This can happen if we get a NOT_EXECUTED scan if the scanner decides not to
                // run the scan
                if (scanId != null && !checkInvalidScanID(scanId)) {
                    ids.add(scanId);
                }
            }
        } else if (getScanId() != null && !checkInvalidScanID(getScanId()))  {
            // If we are using an older version of the signature scanner, prior to 2023.1.0,
            // the scans field will not exist. Fallback to seeing if we have a high level scan ID
            ids.add(getScanId());
        }
        
        return ids;
    }
    private boolean checkInvalidScanID(String scanId) {
        // if BlackDuck returns an invalid scanID (containing only zeros),
        // this method will guard.

        String regex = "^0+-0+-0+-0+-0+$";
        return scanId.matches(regex);
    }
}
