package com.telemetryai.backend.ingestion;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchemaIndex {
    private final int version;
    private final Map<String, EventSchema> events;

    public SchemaIndex(int version, Map<String, EventSchema> events) {
        this.version = version;
        this.events = events;
    }

    public int getVersion() {
        return version;
    }

    public EventSchema getEvent(String eventName) {
        if (eventName == null) {
            return null;
        }
        return events.get(eventName.toLowerCase());
    }

    public static SchemaIndex from(JsonNode schema) {
        int version = schema.has("schemaVersion") ? schema.get("schemaVersion").asInt(1) : 1;
        Map<String, EventSchema> eventMap = new HashMap<>();

        JsonNode eventsNode = schema.get("events");
        if (eventsNode != null && eventsNode.isArray()) {
            for (JsonNode event : eventsNode) {
                String name = event.has("eventName") ? event.get("eventName").asText() : null;
                if (name == null) {
                    continue;
                }
                String description = event.has("eventDescription")
                    ? event.get("eventDescription").asText(null)
                    : null;
                Map<String, PropertySchema> props = new HashMap<>();
                JsonNode propsNode = event.get("properties");
                if (propsNode != null && propsNode.isArray()) {
                    for (JsonNode prop : propsNode) {
                        String key = prop.has("key") ? prop.get("key").asText() : null;
                        if (key == null) {
                            continue;
                        }
                        String type = prop.has("type") ? prop.get("type").asText() : null;
                        boolean required = prop.has("required") && prop.get("required").asBoolean(false);
                        Set<String> allowed = new HashSet<>();
                        JsonNode allowedNode = prop.get("allowed");
                        if (allowedNode != null && allowedNode.isArray()) {
                            for (JsonNode a : allowedNode) {
                                allowed.add(a.asText());
                            }
                        }
                        props.put(key.toLowerCase(), new PropertySchema(key, type, required, allowed));
                    }
                }
                eventMap.put(name.toLowerCase(), new EventSchema(name, description, props));
            }
        }
        return new SchemaIndex(version, eventMap);
    }

    public static final class EventSchema {
        private final String name;
        private final String description;
        private final Map<String, PropertySchema> properties;

        public EventSchema(String name, String description, Map<String, PropertySchema> properties) {
            this.name = name;
            this.description = description;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Map<String, PropertySchema> getProperties() {
            return properties;
        }
    }

    public static final class PropertySchema {
        private final String key;
        private final String type;
        private final boolean required;
        private final Set<String> allowed;

        public PropertySchema(String key, String type, boolean required, Set<String> allowed) {
            this.key = key;
            this.type = type;
            this.required = required;
            this.allowed = allowed;
        }

        public String getKey() {
            return key;
        }

        public String getType() {
            return type;
        }

        public boolean isRequired() {
            return required;
        }

        public Set<String> getAllowed() {
            return allowed;
        }
    }
}
