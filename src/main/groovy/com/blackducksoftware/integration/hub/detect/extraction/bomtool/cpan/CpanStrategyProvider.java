package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class CpanStrategyProvider extends StrategyProvider {

    @Autowired
    protected CpanCliStrategy cpanCliStrategy;

    @Override
    public void init() {

        add(cpanCliStrategy);

    }

}
