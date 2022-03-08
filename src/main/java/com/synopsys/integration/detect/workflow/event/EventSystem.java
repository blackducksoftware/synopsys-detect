package com.synopsys.integration.detect.workflow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSystem {
    private final Map<EventType, List<EventListener>> eventListenerMap = new HashMap<>();

    public <T> void publishEvent(EventType<T> event, T payload) {
        for (EventListener listener : safelyGetListeners(event)) {
            listener.eventOccurred(payload);
        }
    }

    public <T> void registerListener(EventType<T> event, EventListener<T> listener) {
        safelyGetListeners(event).add(listener);
    }

    public <T> void unregisterListener(EventType<T> event, EventListener<T> listener) {
        safelyGetListeners(event).remove(listener);
    }

    private List<EventListener> safelyGetListeners(EventType event) {
        List<EventListener> listeners;
        if (eventListenerMap.containsKey(event)) {
            listeners = eventListenerMap.get(event);
        } else {
            listeners = new ArrayList<>();
            eventListenerMap.put(event, listeners);
        }
        return listeners;
    }
}
