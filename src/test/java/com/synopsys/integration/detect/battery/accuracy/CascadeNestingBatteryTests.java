package com.synopsys.integration.detect.battery.accuracy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.battery.util.assertions.FormattedOutputAssert;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detector.base.DetectorStatusCode;

@Tag("battery")
class CascadeNestingBatteryTests { //TODO(detector-tests): Way too much noise in the test setup, name should be optional, prefix should be optional. Tools value assumed, directory auto-generated.
    @Test
    void mavenCliDoesNotNest() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("maven-cli-no-nest", "none");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("maven-cli-no-nest");
        test.sourceFileNamed("pom.xml");
        test.sourceFileNamed("child/pom.xml");
        test.executable(DetectProperties.DETECT_MAVEN_PATH, "[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ example-maven-travis ---\n"
            + "[INFO] com.blackducksoftware.test:example-maven-travis:jar:0.1.0-SNAPSHOT\n"
            + "[INFO] +- com.google.code.findbugs:jsr305:jar:3.0.1:compile");
        DetectOutput output = test.run();

        FormattedOutputAssert statusAssert = new FormattedOutputAssert(output.getStatusJson());
        statusAssert.assertDetectorCount(1, "Expected only the Maven CLI to have run.");
        statusAssert.assertDetectableStatusNamed("Maven CLI", DetectorStatusCode.PASSED);
    }
}
