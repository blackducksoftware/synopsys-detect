package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

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
        StringTokenizer tokenizer = yarnLockLineAnalyzer.createDependencySpecTokenizer(dependencySpec);
        String name = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        String version = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        logger.info("*** parsed dep '{}' to {}:{}", dependencySpec, name, version);
        return new YarnLockDependency(name, version, optional);
        // TODO orig code supported colon separator (see below)
    }

    //    private ParsedYarnLockDependency parseDependencyFromLine(String line) {
    //        String[] pieces;
    //        if (line.contains(":")) {
    //            pieces = StringUtils.split(line, ":", 2);
    //        } else {
    //            pieces = StringUtils.split(line, " ", 2);
    //        }
    //        return new ParsedYarnLockDependency(removeWrappingQuotes(pieces[0]), removeWrappingQuotes(pieces[1]));
    //    }

}
