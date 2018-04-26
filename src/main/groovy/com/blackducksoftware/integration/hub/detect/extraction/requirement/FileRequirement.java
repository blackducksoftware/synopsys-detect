package com.blackducksoftware.integration.hub.detect.extraction.requirement;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.bucket.Requirement;

public class FileRequirement<C extends ExtractionContext> extends Requirement<C, File> {

    public String filename;

}
