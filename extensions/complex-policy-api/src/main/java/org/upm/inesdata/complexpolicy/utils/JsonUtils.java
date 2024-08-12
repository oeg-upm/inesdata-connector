package org.upm.inesdata.complexpolicy.utils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class JsonUtils {
    private JsonUtils() {
    }

    public static JsonObject parseJsonObj(String string) {
        try (var reader = Json.createReader(new StringReader(string))) {
            return reader.readObject();
        }
    }

    public static JsonValue parseJsonValue(String string) {
        try (var reader = Json.createReader(new StringReader(string))) {
            return reader.readValue();
        }
    }

    public static String toJson(JsonValue json) {
        if (json == null) {
            return "null";
        }

        var sw = new StringWriter();
        try (var writer = Json.createWriter(sw)) {
            writer.write(json);
            return sw.toString();
        }
    }

    public static String toJsonPretty(JsonValue json) {
        if (json == null) {
            return "null";
        }

        var config = Map.of(JsonGenerator.PRETTY_PRINTING, true);

        var sw = new StringWriter();
        try (var writer = Json.createWriterFactory(config).createWriter(sw)) {
            writer.write(json);
            return sw.toString();
        }
    }

}
