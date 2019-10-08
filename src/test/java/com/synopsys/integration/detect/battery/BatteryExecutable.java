package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.synopsys.integration.detect.configuration.DetectProperty;

public abstract class BatteryExecutable {
    public final DetectProperty detectProperty;

    protected BatteryExecutable(final DetectProperty detectProperty) {
        this.detectProperty = detectProperty;
    }

    public abstract File createExecutable(final int id, final File mockDirectory, AtomicInteger commandCount) throws IOException;
}
