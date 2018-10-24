package com.blackducksoftware.integration.hub.detect.workflow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSystem {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Map<Event, List<EventListener>> eventListenerMap = new HashMap<>();

    public void publishEvent(Event event, Object payload) {
        if (event.getEventClass().isAssignableFrom(payload.getClass())) {
            for (EventListener listener : safelyGetListeners(event)) {
                listener.eventOccured(payload);
            }
        } else {
            logger.warn("An event was published with the incorrect event type.");
        }
    }

    public void registerListener(Event event, EventListener listener) {
        safelyGetListeners(event).add(listener);
    }

    public void unregisterListener(Event event, EventListener listener) {
        safelyGetListeners(event).remove(listener);
    }

    private List<EventListener> safelyGetListeners(Event event) {
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
