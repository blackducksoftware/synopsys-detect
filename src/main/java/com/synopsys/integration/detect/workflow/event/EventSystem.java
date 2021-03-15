/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSystem {
    private final Map<EventType, List<EventListener>> eventListenerMap = new HashMap<>();

    public <T> void publishEvent(final EventType<T> event, final T payload) {
        for (final EventListener listener : safelyGetListeners(event)) {
            listener.eventOccured(payload);
        }
    }

    public <T> void registerListener(final EventType<T> event, final EventListener<T> listener) {
        safelyGetListeners(event).add(listener);
    }

    public <T> void unregisterListener(final EventType<T> event, final EventListener<T> listener) {
        safelyGetListeners(event).remove(listener);
    }

    private List<EventListener> safelyGetListeners(final EventType event) {
        final List<EventListener> listeners;
        if (eventListenerMap.containsKey(event)) {
            listeners = eventListenerMap.get(event);
        } else {
            listeners = new ArrayList<>();
            eventListenerMap.put(event, listeners);
        }
        return listeners;
    }
}
