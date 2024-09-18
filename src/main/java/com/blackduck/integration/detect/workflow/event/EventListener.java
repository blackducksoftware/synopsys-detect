package com.blackduck.integration.detect.workflow.event;

public interface EventListener<T> {
    void eventOccurred(T event);
}
