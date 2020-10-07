package com.synopsys.integration.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Black Duck Collection
public class Bdc {
    @SafeVarargs
    public static <T> List<T> listFromArrayArgs(T[] array, T... args) {
        List<T> arguments = new ArrayList<>();
        if (array != null) {
            arguments.addAll(Arrays.asList(array));
        }
        arguments.addAll(Arrays.asList(args));
        return arguments;
    }
}
