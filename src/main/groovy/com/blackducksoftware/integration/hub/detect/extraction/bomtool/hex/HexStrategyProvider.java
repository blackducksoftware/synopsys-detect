package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class HexStrategyProvider extends StrategyProvider {

    @Autowired
    public RebarStrategy rebarStrategy;

    @Override
    public void init() {
        add(rebarStrategy);
    }

}
