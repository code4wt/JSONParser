package com.code4wt.jsonparser.model;

import com.code4wt.jsonparser.exception.JsonTypeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by code4wt on 17/5/19.
 */
public class JsonArray implements Json {

    private List list = new ArrayList();

    public void add(Object obj) {
        list.add(obj);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public JsonObject getJsonObject(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JsonObject)) {
            throw new JsonTypeException("Type of value is not JsonObject");
        }

        return (JsonObject) obj;
    }

    public JsonArray getJsonArray(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JsonArray)) {
            throw new JsonTypeException("Type of value is not JsonArray");
        }

        return (JsonArray) obj;
    }

    @Override
    public String toString() {
        return "JsonArray{" +
                "list=" + list +
                '}';
    }
}
