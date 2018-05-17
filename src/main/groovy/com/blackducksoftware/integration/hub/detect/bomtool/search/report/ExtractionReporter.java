package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

@Component
public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);

    public void startedExtraction(final Strategy strategy, final ExtractionContext context) {
        logger.info(ReportConstants.SEPERATOR);
        final String strategyName = strategy.getBomToolType() + " - " + strategy.getName();
        logger.info("Starting extraction: " + strategyName);
        logger.info("Identifier: " + Integer.toString(context.hashCode()));
        logger.info("Extractor: " + strategy.getExtractorClass().getSimpleName());
        logger.info("Context: " + strategy.getExtractionContextClass().getSimpleName());
        printObject(context);
        logger.info(ReportConstants.SEPERATOR);
    }

    public void endedExtraction(final Extraction result) {
        logger.info(ReportConstants.SEPERATOR);
        logger.info("Finished extraction: " + result.result.toString());
        logger.info("Code locations found: " + result.codeLocations.size());
        if (result.result == ExtractionResult.Exception) {
            logger.info("Exception:", result.error);
        } else if (result.result == ExtractionResult.Failure) {
            logger.info(result.description);
        }
        logger.info(ReportConstants.SEPERATOR);
    }

    private void printObject(final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            final String name = field.getName();
            String value = "unknown";
            try {
                value = field.get(guy).toString();
            } catch (final Exception e) {

            }
            logger.info(name + " : " + value);
        }

    }
}
