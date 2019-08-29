package com.synopsys.integration.detectable.detectables.pear.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectable;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearCliExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PearCliDetectableTest {
    public static final String PACKAGE_XML_FILENAME = "package.xml";

    @Test
    public void testApplicable() {

        final PearResolver pearResolver = null;
        final PearCliExtractor pearCliExtractor = null;
        final PearCliDetectableOptions pearCliDetectableOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("package.xml");

        final PearCliDetectable detectable = new PearCliDetectable(environment, fileFinder, pearResolver, pearCliExtractor, pearCliDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
