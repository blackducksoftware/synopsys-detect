package com.synopsys.integration.detect.workflow.blackduck;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;

public class ExclusionPatternCreatorTest {
    @ParameterizedTest
    @MethodSource("inputPatternsToExclusionsProvider")
    public void testProducesCorrectExclusions(List<String> providedPatterns, List<String> resultingExclusions) throws IOException {
        File root = new File("root");
        root.mkdir();
        File sub1 = new File(root, "sub1");
        sub1.mkdir();
        File sub2 = new File(root, "sub2");
        sub2.mkdir();
        File sub1Sub1 = new File(sub1, "sub1Sub1");
        sub1Sub1.mkdir();
        File sub1Sub2 = new File(sub1, "sub1Sub2");
        sub1Sub2.mkdir();
        File sub2Sub1 = new File(sub2, "sub2Sub1");
        sub2Sub1.mkdir();

        DetectExcludedDirectoryFilter filter = new DetectExcludedDirectoryFilter(providedPatterns);
        ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(new SimpleFileFinder(), file -> filter.isExcluded(file), root);
        assertEqualCollections(resultingExclusions, exclusionPatternCreator.determineExclusionPatterns(false, 3, providedPatterns));

        FileUtils.deleteDirectory(root);
    }

    static Stream<Arguments> inputPatternsToExclusionsProvider() {
        // Stream of single input patterns and the list of patterns that they should resolve to
        // NOTE: recognition of path patterns is dependent on filter's implementation of path matching (glob vs regex)
        return Stream.of(
            arguments(Collections.singletonList("**ub2"), Arrays.asList("/sub1/sub1Sub2/", "/sub2/")),
            arguments(Collections.singletonList("**root/sub1/sub1*"), Arrays.asList("/sub1/sub1Sub1/", "/sub1/sub1Sub2/")),
            arguments(Collections.singletonList("**root/*/*"), Arrays.asList("/sub1/sub1Sub1/", "/sub1/sub1Sub2/", "/sub2/sub2Sub1/")),
            arguments(Collections.singletonList("*1Sub2"), Collections.singletonList("/sub1/sub1Sub2/")),
            arguments(Collections.singletonList("sub?"), Arrays.asList("/sub1/", "/sub2/")),
            arguments(Collections.singletonList("/blackduck-*/"), Collections.emptyList())
        );
    }

    private void assertEqualCollections(Collection<String> collection1, Collection<String> collection2) {
        for (String item1 : collection1) {
            if (!collection2.contains(item1)) {
                Assertions.fail();
            }
        }
        for (String item2 : collection2) {
            if (!collection1.contains(item2)) {
                Assertions.fail();
            }
        }
    }

    @Test
    public void testDoesNotCreateRedundantExclusions() throws IOException {
        File root = new File("root");
        root.mkdir();
        File foo = new File("root", "foo");
        foo.mkdir();
        File bar = new File(foo, "bar");
        bar.mkdir();

        List<String> toExclude = Arrays.asList("foo", "bar");

        DetectExcludedDirectoryFilter filter = new DetectExcludedDirectoryFilter(toExclude);
        ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(new SimpleFileFinder(), file -> filter.isExcluded(file), root);
        Set<String> exlusionPatterns = exclusionPatternCreator.determineExclusionPatterns(false, 2, toExclude);

        Assertions.assertEquals(1, exlusionPatterns.size());
        Assertions.assertTrue(exlusionPatterns.contains("/foo/"));
        Assertions.assertFalse(exlusionPatterns.contains("/foo/bar/"));

        FileUtils.deleteDirectory(root);
    }
}
