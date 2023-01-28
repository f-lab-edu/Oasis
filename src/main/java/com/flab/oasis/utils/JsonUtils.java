package com.flab.oasis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parseObjectToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode parseStringToJsonNode(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<JsonNode> parseJsonNodeToJsonNodeList(JsonNode jsonNode) {
        List<JsonNode> jsonNodeList = new ArrayList<>();
        jsonNode.fieldNames().forEachRemaining(key -> jsonNodeList.add(getJsonNodeByKey(key, jsonNode)));

        return jsonNodeList;
    }

    public static List<JsonNode> getValueListFromJsonNode(List<String> keyList,JsonNode jsonNode) {
        return keyList.stream().map(key -> getJsonNodeByKey(key, jsonNode)).collect(Collectors.toList());
    }

    private static JsonNode getJsonNodeByKey(String key, JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode.get(key)).orElseThrow(() -> new RuntimeException("Key가 존재하지 않습니다!"));
    }
}
