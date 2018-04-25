package com.blackducksoftware.integration.hub.detect.bomtool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

interface PropertySetter<T> {
    boolean setProperty(String value, T result);
}

@Component
public class BomToolApplicableBuilder<NpmCliApplicableResult> {

    @Autowired
    protected ExecutableManager executableManager;

    @Autowired
    protected DetectConfiguration detectConfiguration;


    Map<ExecutableType, Method> requiredExecutables = new HashMap<>();
    Map<String, Method> requiredFiles = new HashMap<>();

    public BomToolApplicableBuilder requireExecutable(final ExecutableType type, final Method setter) {
        requiredExecutables.put(type, setter);
        return this;
    }

    public BomToolApplicableBuilder requireFile(final String file, final PropertySetter<NpmCliApplicableResult> setter) {
        //requiredFiles.put(file, setter);
        setter.setProperty(file, new NpmCliApplicableResult());
        return this;
    }

    public void asContext(final Class<?> clazz) {

    }

    public void withExtractor() {

    }

    public void build(final Class<?> clazz) {
        try {
            final Constructor<?> cons = clazz.getConstructor();
            final Object o = cons.newInstance();
            requiredExecutables.entrySet().stream().forEach(it -> evaluate(it.getKey(), it.getValue(), o));
        }catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void evaluate(final ExecutableType type, final Method setter, final Object o) {
        final String exe = executableManager.getExecutablePath(type, true, detectConfiguration.getSourcePath());
        if (exe != null) {
            try {
                setter.invoke(o, exe);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

