/*
 * common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.log.IntLogger;

public class ProxyUtil {
    /**
     * Checks the list of user defined host names that should be connected to directly and not go through the proxy. If the hostToMatch matches any of these hose names then this method returns true.
     */
    public static boolean shouldIgnoreHost(final String hostToMatch, final List<Pattern> ignoredProxyHostPatterns) {
        if (StringUtils.isBlank(hostToMatch) || ignoredProxyHostPatterns == null || ignoredProxyHostPatterns.isEmpty()) {
            return false;
        }

        for (final Pattern ignoredProxyHostPattern : ignoredProxyHostPatterns) {
            final Matcher m = ignoredProxyHostPattern.matcher(hostToMatch);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldIgnoreUrl(final String url, final List<Pattern> ignoredProxyHostPatterns, IntLogger logger) {
        try {
            return ProxyUtil.shouldIgnoreHost(new URL(url).getHost(), ignoredProxyHostPatterns);
        } catch (MalformedURLException e) {
            logger.error("Unable to decide if proxy should be used for the given host, will use proxy.");
            return false;
        }
    }

}