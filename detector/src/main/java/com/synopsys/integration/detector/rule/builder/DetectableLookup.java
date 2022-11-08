package com.synopsys.integration.detector.rule.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class DetectableLookup {
    private final Object detectableFactory;

    public DetectableLookup(Object detectableFactory) {
        this.detectableFactory = detectableFactory;
    }

    public DetectableDefinition forClass(Class<?> detectableClass) {
        DetectableCreatable creatable = findDetectableCreator(detectableFactory, detectableClass);
        DetectableInfo info = findDetectableInfo(detectableClass);
        return new DetectableDefinition(creatable, info.name(), info.forge(), info.language(), info.requirementsMarkdown(), info.accuracy());
    }

    @NotNull
    private static DetectableInfo findDetectableInfo(Class<?> detectableClass) {
        return Arrays.stream(detectableClass.getAnnotations())
            .filter(DetectableInfo.class::isInstance)
            .map(DetectableInfo.class::cast)
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }

    /*
    This reduces noise. It finds methods with CLASS createCLASS(DetectEnvironment) on the factory object.
     */
    private static <T extends Detectable> DetectableCreatable findDetectableCreator(Object factory, Class<?> target) {
        Method methods[] = factory.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!method.getName().startsWith("create")) {
                continue;
            }
            if (method.getParameterTypes().length != 1) {
                continue;
            }
            if (!method.getParameterTypes()[0].isAssignableFrom(DetectableEnvironment.class)) {
                continue;
            }
            if (target.isAssignableFrom(method.getReturnType())) {
                return (environment) -> {
                    try {
                        return (T) method.invoke(factory, environment);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException();
                    }
                };
            }
        }
        throw new RuntimeException();
    }
}
