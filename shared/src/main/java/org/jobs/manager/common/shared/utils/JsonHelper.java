package org.jobs.manager.common.shared.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Slf4j
public final class JsonHelper {

    private static final ObjectMapper MAPPER;

    static {
        SimpleModule additionalModule = new SimpleModule();

        MAPPER = new ObjectMapper()
                .findAndRegisterModules()
                .registerModule(additionalModule)
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .setDefaultPrettyPrinter(new MinimalPrettyPrinter())
                .configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public static <T> String toJson(T obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error convert to JSON {}", obj, e);
            String name = obj != null ? obj.getClass().getSimpleName() : "empty";
            throw new RuntimeException("Error convert to the model " + name, e);
        }
    }

    public static <T> Optional<T> fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json))
            return Optional.empty();
        try {
            return Optional.of(MAPPER.readValue(json, clazz));
        } catch (Exception e) {
            String name = clazz != null ? clazz.getSimpleName() : "empty";
            log.error("Error convert to the name {} json {}", name, json, e);
            throw new RuntimeException("Error convert to the model " + name, e);
        }
    }
}
