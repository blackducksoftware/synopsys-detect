package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import freemarker.template.TemplateException;

public abstract class BatteryExecutableCreator {
    public abstract File createExecutable(final int id, final File mockDirectory, AtomicInteger commandCount) throws IOException, TemplateException;
}
