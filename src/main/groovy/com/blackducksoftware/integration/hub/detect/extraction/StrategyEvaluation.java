package com.blackducksoftware.integration.hub.detect.extraction;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

public class StrategyEvaluation {
    public Strategy strategy;
    public StrategyEnvironment environment;
    public ExtractionContext context;

    public StrategyResult searchable;
    public StrategyResult applicable;
    public StrategyResult extractable;

    public Extraction extraction;

    public StrategyEvaluation(final Strategy strategy, final StrategyEnvironment environment, final ExtractionContext context) {
        this.strategy = strategy;
        this.environment = environment;
        this.context = context;
    }

    public boolean isSearchable() {
        if (this.searchable != null) {
            return this.searchable.getPassed();
        }else {
            return false;
        }
    }

    public boolean isApplicable() {
        if (isSearchable()) {
            if (this.applicable != null) {
                return this.applicable.getPassed();
            }
        }
        return false;
    }

    public boolean isExtractable() {
        if (isApplicable()) {
            if (this.extractable != null) {
                return this.extractable.getPassed();
            }
        }
        return false;
    }

    public boolean isExtractionSuccess() {
        if (isExtractable()) {
            if (this.extraction != null) {
                return this.extraction.result == ExtractionResult.Success;
            }
        }
        return false;
    }

}
