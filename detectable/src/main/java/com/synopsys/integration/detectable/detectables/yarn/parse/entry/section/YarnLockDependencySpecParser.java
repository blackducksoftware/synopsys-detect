/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;

public class YarnLockDependencySpecParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockDependencySpecParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    public YarnLockDependency parse(String dependencySpec, boolean optional) {
        StringTokenizer tokenizer = TokenizerFactory.createDependencySpecTokenizer(dependencySpec);
        String name = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        String version = yarnLockLineAnalyzer.unquote(tokenizer.nextToken(":").trim());
        logger.trace("\tdependency: name: {}, version: {} (optional: {})", name, version, optional);
        return new YarnLockDependency(name, version, optional);
    }
}
