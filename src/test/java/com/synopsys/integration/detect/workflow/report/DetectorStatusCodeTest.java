package com.synopsys.integration.detect.workflow.report;

import java.io.File;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detector.base.DetectorResultStatusCodeLookup;
import com.synopsys.integration.detector.result.DetectorResult;

public class DetectorStatusCodeTest {

    @Test
    public void ensureDetectorResultsPresentInStatusLookup() {
        Set<? extends Class<?>> classes = getClasses("detector/src/main/java/com/synopsys/integration/detector/result", "com.synopsys.integration.detector.result");
        classes.remove(DetectorResult.class); //Need to remove the base class as it shouldn't be mapped.
        assertAllPresent(classes);
    }

    @Test
    public void ensureDetectableResultsPresentInStatusLookup() {
        Set<? extends Class<?>> classes = getClasses(
            "detectable/src/main/java/com/synopsys/integration/detectable/detectable/result",
            "com.synopsys.integration.detectable.detectable.result"
        );
        classes.remove(DetectableResult.class);//Need to remove the base class as it shouldn't be mapped.
        assertAllPresent(classes);
    }

    private void assertAllPresent(Set<? extends Class<?>> classes) {
        Assertions.assertTrue(classes.size() > 5, "Should have at least found a few result classes.");
        classes.forEach(it -> Assertions.assertNotNull(
            DetectorResultStatusCodeLookup.standardLookup.getStatusCode(it),
            "Expected " + it.getSimpleName() + " to be in the lookup."
        ));
    }

    Set<? extends Class<?>> getClasses(String directory, String packageName) {
        return Bds.of(new File(directory).listFiles())
            .filter(it -> !it.isDirectory())
            .map(File::getName)
            .map(it -> it.replace(".java", ""))
            .map(it -> {
                try {
                    return Class.forName(packageName + "." + it);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toSet();
    }

}
