package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bucket.Requirement;

public class Strategy<C extends ExtractionContext, E extends Extractor>  {

    public List<Requirement<C, ?>> requirements;
    public Class<C> extractionContextClass;
    public Class<E> extractorClass;

}
