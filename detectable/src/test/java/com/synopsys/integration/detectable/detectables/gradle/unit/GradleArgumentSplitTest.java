package com.synopsys.integration.detectable.detectables.gradle.unit;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleRunner;

public class GradleArgumentSplitTest {

    @Test
    public void testRefreshDependenciesKept() {
        List<String> arguments = invokeSplit("arg1 refresh-dependencies");
        Assertions.assertEquals(2, arguments.size());
        Assertions.assertEquals("arg1", arguments.get(0));
        Assertions.assertEquals("refresh-dependencies", arguments.get(1));
    }

    @Test
    public void testDependenciesRemoved() {
        List<String> arguments = invokeSplit("arg1 dependencies");
        Assertions.assertEquals(1, arguments.size());
        Assertions.assertEquals("arg1", arguments.get(0));
    }

    @Test
    public void testDependenciesRemovedMiddle() {
        List<String> arguments = invokeSplit("arg1 dependencies arg2");
        Assertions.assertEquals(2, arguments.size());
        Assertions.assertEquals("arg1", arguments.get(0));
        Assertions.assertEquals("arg2", arguments.get(1));
    }

    @Test
    public void testTwoArguments() {
        List<String> arguments = invokeSplit("arg1 arg2");
        Assertions.assertEquals(2, arguments.size());
        Assertions.assertEquals("arg1", arguments.get(0));
        Assertions.assertEquals("arg2", arguments.get(1));
    }

    @Test
    public void testEmptyArgumentsIsEmpty() {
        Assertions.assertEquals(0, invokeSplit("").size());
    }

    @Test
    public void testNullArgumentsIsEmpty() {
        Assertions.assertEquals(0, invokeSplit(null).size());
    }

    @Test //Not sure about this one, but just making sure we don't do something unintentional as the Runner modifies the list later. - jp
    public void testArgumentCollectionMutable() {
        List<String> arguments = invokeSplit("");
        int starting = arguments.size();
        arguments.add("");
        Assertions.assertEquals(starting + 1, arguments.size());
    }

    private List<String> invokeSplit(@Nullable String argument) {
        GradleRunner gradleRunner = new GradleRunner(null);
        return gradleRunner.splitUserArguments(argument);
    }

}
