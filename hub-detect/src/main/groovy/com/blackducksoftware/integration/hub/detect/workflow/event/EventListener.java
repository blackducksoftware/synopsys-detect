package com.blackducksoftware.integration.hub.detect.workflow.event;

public interface EventListener<T> {
    void eventOccured(T event);
}
