package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

@Component
public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);

    public void startedExtraction(final Strategy strategy, final ExtractionContext context) {
        printSeperator();
        printSeperator();
        final String strategyName = strategy.getBomToolType() + " - " + strategy.getName();
        logger.info("Starting extraction: " + strategyName);
        logger.info("Extractor: " + strategy.getExtractorClass().getSimpleName());
        logger.info("Context: " + strategy.getExtractionContextClass().getSimpleName());
        printObject(context);
        printSeperator();
    }

    public void endedExtraction(final Extraction result) {
        printSeperator();
        logger.info("Finished extraction: " + result.result.toString());
        logger.info("Code locations found: " + result.codeLocations.size());
        if (result.result == ExtractionResult.Exception) {
            logger.info("Exception:", result.error);
        } else if (result.result == ExtractionResult.Failure) {
            logger.info(result.description);
        }
        printSeperator();
    }

    private void printSeperator() {
        logger.info("------------------------------------------------------------------------------------------------------");
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
