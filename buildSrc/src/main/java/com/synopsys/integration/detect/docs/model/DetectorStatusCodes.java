package com.synopsys.integration.detect.docs.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.detect.docs.copied.HelpJsonDetectorStatusCode;

public class DetectorStatusCodes {
    private final Set<HelpJsonDetectorStatusCode> statusCodes;

    public DetectorStatusCodes(List<HelpJsonDetectorStatusCode> statusCodes) {
        this.statusCodes = new HashSet<>(statusCodes);
    }

    public List<HelpJsonDetectorStatusCode> getStatusCodes() {
        List<HelpJsonDetectorStatusCode> sortedCodes = new ArrayList<>(statusCodes);
        sortedCodes.sort(new HelpJsonDetectorStatusCodeComparator());
        return sortedCodes;
    }

    private static class HelpJsonDetectorStatusCodeComparator implements Comparator<HelpJsonDetectorStatusCode> {
        @Override
        public int compare(HelpJsonDetectorStatusCode o1, HelpJsonDetectorStatusCode o2) {
            return o1.getStatusCode().compareTo(o2.getStatusCode());
        }
    }

}
