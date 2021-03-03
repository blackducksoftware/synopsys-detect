/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.detect.docs.copied.HelpJsonDetectorStatusCode;

public class DetectorStatusCodes {

    private Set<HelpJsonDetectorStatusCode> statusCodes;

    public DetectorStatusCodes(final List<HelpJsonDetectorStatusCode> statusCodes) {
        this.statusCodes = new HashSet<>(statusCodes);
    }

    public List<HelpJsonDetectorStatusCode> getStatusCodes() {
        List<HelpJsonDetectorStatusCode> sortedCodes = new ArrayList<>(statusCodes);
        sortedCodes.sort(new HelpJsonDetectorStatusCodeComparator());
        return sortedCodes;
    }

    private static class HelpJsonDetectorStatusCodeComparator implements Comparator<HelpJsonDetectorStatusCode> {

        @Override
        public int compare(final HelpJsonDetectorStatusCode o1, final HelpJsonDetectorStatusCode o2) {
            return o1.getStatusCode().compareTo(o2.getStatusCode());
        }
    }
}
