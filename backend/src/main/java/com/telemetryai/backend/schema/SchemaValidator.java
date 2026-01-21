package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SchemaValidator {
    private static final Set<String> ALLOWED_TYPES = Set.of("string", "number", "boolean", "object", "array");

    private SchemaValidator() {
    }

    public static List<String> validate(JsonNode schema, int maxEvents, int maxProperties, int maxDescriptionLength) {
        List<String> errors = new ArrayList<>();
        if (schema == null || !schema.isObject()) {
            errors.add("Schema root must be an object");
            return errors;
        }

        JsonNode events = schema.get("events");
        if (events == null || !events.isArray()) {
            errors.add("events must be an array");
            return errors;
        }

        if (events.size() > maxEvents) {
            errors.add("events size exceeds limit: " + maxEvents);
        }

        Set<String> eventNames = new HashSet<>();
        for (int i = 0; i < events.size(); i++) {
            JsonNode event = events.get(i);
            if (event == null || !event.isObject()) {
                errors.add("event at index " + i + " must be an object");
                continue;
            }

            String eventName = textValue(event.get("eventName"));
            if (eventName == null || eventName.isBlank()) {
                errors.add("eventName is required at index " + i);
            } else {
                String key = eventName.toLowerCase(Locale.ROOT);
                if (!eventNames.add(key)) {
                    errors.add("eventName must be unique: " + eventName);
                }
            }

            JsonNode eventDescription = event.get("eventDescription");
            if (eventDescription != null && !eventDescription.isNull()) {
                if (!eventDescription.isTextual()) {
                    errors.add("eventDescription must be string for " + eventName);
                } else if (eventDescription.textValue().length() > maxDescriptionLength) {
                    errors.add("eventDescription too long for " + eventName);
                }
            }

            JsonNode required = event.get("required");
            if (required != null && !required.isNull() && !required.isBoolean()) {
                errors.add("required must be boolean for " + eventName);
            }

            JsonNode properties = event.get("properties");
            if (properties == null || properties.isNull()) {
                continue;
            }
            if (!properties.isArray()) {
                errors.add("properties must be array for " + eventName);
                continue;
            }
            if (properties.size() > maxProperties) {
                errors.add("properties size exceeds limit for " + eventName);
            }

            Set<String> propertyKeys = new HashSet<>();
            for (int p = 0; p < properties.size(); p++) {
                JsonNode prop = properties.get(p);
                if (prop == null || !prop.isObject()) {
                    errors.add("property at index " + p + " must be object for " + eventName);
                    continue;
                }

                String key = textValue(prop.get("key"));
                if (key == null || key.isBlank()) {
                    errors.add("property.key required for " + eventName);
                } else {
                    String lowered = key.toLowerCase(Locale.ROOT);
                    if (!propertyKeys.add(lowered)) {
                        errors.add("property.key must be unique for " + eventName + ": " + key);
                    }
                }

                String type = textValue(prop.get("type"));
                if (type == null || type.isBlank()) {
                    errors.add("property.type required for " + eventName + ": " + key);
                } else if (!ALLOWED_TYPES.contains(type)) {
                    errors.add("property.type invalid for " + eventName + ": " + key);
                }

                JsonNode propRequired = prop.get("required");
                if (propRequired != null && !propRequired.isNull() && !propRequired.isBoolean()) {
                    errors.add("property.required must be boolean for " + eventName + ": " + key);
                }

                JsonNode allowed = prop.get("allowed");
                if (allowed != null && !allowed.isNull()) {
                    if (!allowed.isArray()) {
                        errors.add("allowed must be array for " + eventName + ": " + key);
                    } else if (type != null && !type.isBlank()) {
                        if (!"string".equals(type) && !"number".equals(type)) {
                            errors.add("allowed only valid for string/number for " + eventName + ": " + key);
                        } else {
                            for (int a = 0; a < allowed.size(); a++) {
                                JsonNode value = allowed.get(a);
                                boolean valid = "string".equals(type) ? value.isTextual() : value.isNumber();
                                if (!valid) {
                                    errors.add("allowed value type mismatch for " + eventName + ": " + key);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return errors;
    }

    private static String textValue(JsonNode node) {
        return node != null && node.isTextual() ? node.textValue() : null;
    }
}
