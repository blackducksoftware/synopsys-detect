package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import freemarker.template.TemplateException;

public abstract class BatteryExecutableCreator {
    public abstract File createExecutable(int id, BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException;
}
