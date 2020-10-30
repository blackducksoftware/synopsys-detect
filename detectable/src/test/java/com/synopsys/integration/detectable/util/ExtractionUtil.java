package com.synopsys.integration.detectable.util;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class ExtractionUtil {
    public static CodeLocation assertAndGetCodeLocationNamed(String name, Extraction extraction) {
        CodeLocation found = null;
        for (CodeLocation it : extraction.getCodeLocations()) {
            if (it.getExternalId().isPresent() && it.getExternalId().get().getName().equals(name)) {
                found = it;
            }
        }
        Assertions.assertNotNull(found, "Missing code location with name: " + name);
        return found;
    }

    public static void assertSuccessWithCodeLocationCount(Extraction extraction, int codeLocationCount) {
        if (extraction.getError() != null) {
            Assertions.assertNull(extraction.getError(), "Extraction should not have an error: " + extraction.getError().getMessage());
        }
        Assertions.assertSame(extraction.getResult(), Extraction.ExtractionResultType.SUCCESS, "Extraction should have been success: " + extraction.getDescription());
        Assertions.assertEquals(codeLocationCount, extraction.getCodeLocations().size(), "Should have produced " + codeLocationCount + " code locations.");

    }
}
