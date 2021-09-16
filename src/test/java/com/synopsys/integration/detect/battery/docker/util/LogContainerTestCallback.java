package com.synopsys.integration.detect.battery.docker.util;

import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;

public class LogContainerTestCallback extends LogContainerResultCallback {
    protected final StringBuilder log = new StringBuilder();

    List collectedFrames = new ArrayList<>();

    boolean collectFrames = false;

    public LogContainerTestCallback() {
        this(false);
    }

    public LogContainerTestCallback(boolean collectFrames) {
        this.collectFrames = collectFrames;
    }

    @Override
    public void onNext(Frame frame) {
        if (collectFrames)
            collectedFrames.add(frame);
        log.append(new String(frame.getPayload()));
    }

    @Override
    public String toString() {
        return log.toString();
    }

    public List getCollectedFrames() {
        return collectedFrames;
    }
}