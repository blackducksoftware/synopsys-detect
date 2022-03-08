package com.synopsys.integration.detect.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.property.Property;

@Disabled // For now these tests are not actually helping anyone. Will be tagged with "lint" in the future.
public class DeprecatedPropertyReferenceTest {
    private final List<String> fileTypesToCheck = Bds.listOf(
        "java",
        "kt",
        "groovy",
        "kts"
    );

    private final List<String> excludedFileNames = Bds.of(Bds.listOf(
        DetectConfigurationFactory.class,
        DetectableOptionFactory.class,
        DetectProperties.class,
        this.getClass()
    )).map(Class::getSimpleName).toList();

    private final List<String> deprecatedPropertyReferenceStrings = Bds.of(DetectProperties.allProperties().getProperties())
        .filter(it -> it.getPropertyDeprecationInfo() != null)
        .map(Property::getKey)
        .map(String::toUpperCase)
        .map(key -> key.replace(".", "_"))
        .toList();

    public DeprecatedPropertyReferenceTest() throws IllegalAccessException {
    }

    @Test
    public void testCodeReferencesToDeprecatedProperties() throws IOException {
        File rootDir = new File("src");
        Collection<File> sourceFiles = FileUtils.listFiles(rootDir, fileTypesToCheck.toArray(ArrayUtils.EMPTY_STRING_ARRAY), true);
        List<File> notAllowedFiles = Bds.of(sourceFiles)
            .filter(file -> !excludedFileNames.contains(FilenameUtils.getBaseName(file.getName())))
            .toList();

        List<String> issues = Bds.listOf();
        for (File file : notAllowedFiles) {
            String fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            for (String referenceString : deprecatedPropertyReferenceStrings) {
                if (fileContents.contains(referenceString)) {
                    issues.add("Illegal use of '$referenceString' found in $file");
                }
            }
        }
        issues.forEach(System.out::println);
        Assertions.assertEquals(0, issues.size(), "One or more issues were found. Please see the log.");
    }
}