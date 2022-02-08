package com.synopsys.integration.detect.workflow.event;

public interface EventListener<T> {
    void eventOccurred(T event);
}
