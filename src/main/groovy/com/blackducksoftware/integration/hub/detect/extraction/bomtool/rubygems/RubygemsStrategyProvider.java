package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class RubygemsStrategyProvider extends StrategyProvider {

    @Autowired
    public GemlockStrategy gemlockStrategy;

    public static final String GEMFILE_LOCK_FILENAME= "Gemfile.lock";

    @Override
    public void init() {

        add(gemlockStrategy);

    }

}
