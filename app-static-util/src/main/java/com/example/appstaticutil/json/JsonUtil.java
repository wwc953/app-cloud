package com.example.appstaticutil.json;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * gosn 工具类
 */
public class JsonUtil {

    private static Gson gson;
    private static ObjectMapper objectMapper;

    static {
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting().disableHtmlEscaping().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());
        gb.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        gb.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gson = gb.create();
        DefaultSerializerProvider.Impl sp = new DefaultSerializerProvider.Impl();
        objectMapper = new ObjectMapper(null, sp, null);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    }

    public static Map convertJsonToMap(String jsonString) {
        try {
            return (Map) objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertJsonToObject(String jsonString, Class<T> modelCalss) {
        try {
            return objectMapper.readValue(jsonString, modelCalss);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertJsonToObject(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getMap4Json(String jsonString) {
        return convertJsonToObject(jsonString, new TypeReference<Map<String, String>>() {
        });
    }

    public static Map<String, Object> getMap4JsonObject(String jsonString) {
        return convertJsonToObject(jsonString, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String convertObjectToJson(Object javaObj) {
        try {
            return objectMapper.writeValueAsString(javaObj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> convertJsonToList(String jsonString, Class<T> pojoClass) {
        return JSONArray.parseArray(jsonString, pojoClass);
    }


}
