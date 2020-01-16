package com.synopsys.integration.detect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.boot.ProductDeciderTest;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.lifecycle.boot.DetectBoot;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.tool.polaris.PolarisTool;

public class DeprecatedPropertyReferenceTest {

    private final List<String> fileTypesToCheck = Arrays.asList(
        ".java",
        ".kt"
    );

    private final List<Class> classesAllowedToReferenceDeprecatedComponents = Arrays.asList(
        Application.class,
        DetectBoot.class,
        ProductDecider.class,
        DetectConfigurationFactory.class,
        DetectableOptionFactory.class,
        PolarisTool.class,
        ProductDeciderTest.class,
        this.getClass()
    );

    private final List<String> filesAllowedToReferenceDeprecatedComponents = fileTypesToCheck.stream()
                                                                                 .map(fileType -> classesAllowedToReferenceDeprecatedComponents.stream()
                                                                                                      .map(clazz -> clazz.getSimpleName() + fileType)
                                                                                                      .collect(Collectors.toList()))
                                                                                 .flatMap(List::stream)
                                                                                 .collect(Collectors.toList());

    @Test
    public void testCodeReferencesToDeprecatedProperties() throws IOException {
        final Set<String> classesInViolation = new HashSet<>();
        final List<Property> deprecatedProperties = getDeprecatedProperties();
        final String[] targetSuffixes = { "java", "groovy" };
        final File rootDir = new File("src");
        final Collection<File> javaFiles = FileUtils.listFiles(rootDir, targetSuffixes, true);
        for (final File javaFile : javaFiles) {
            final String javaFilename = javaFile.getName();
            if (isFileAllowedToReferenceDeprecatedProperties(javaFilename)) {
                continue;
            }
            final String javaFileContents = FileUtils.readFileToString(javaFile, StandardCharsets.UTF_8);
            for (final Property deprecatedProperty : deprecatedProperties) {
                if (javaFileContents.contains(deprecatedProperty.getName())) {
                    System.out.printf("Deprecated property %s is referenced in %s\n", deprecatedProperty.getName(), javaFile.getAbsolutePath());
                    classesInViolation.add(javaFilename);
                }
            }
        }

        Assertions.assertEquals(0, classesInViolation.size(), String.format("The following classes are in violation: %s", StringUtils.join(classesInViolation, ", ")));
    }

    private List<Property> getDeprecatedProperties() {
        return DetectProperties.Companion.getProperties().stream()
                   .filter(it -> it.getPropertyDeprecationInfo() != null)
                   .collect(Collectors.toList());
    }

    private boolean isFileAllowedToReferenceDeprecatedProperties(final String fileName) {
        return filesAllowedToReferenceDeprecatedComponents.contains(fileName);
    }
}
