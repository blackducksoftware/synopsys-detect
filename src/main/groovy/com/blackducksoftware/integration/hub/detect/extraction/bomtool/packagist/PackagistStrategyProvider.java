package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class PackagistStrategyProvider extends StrategyProvider {

    @Autowired
    public ComposerLockStrategy composerLockStrategy;

    @Override
    public void init() {

        add(composerLockStrategy);

    }

}
