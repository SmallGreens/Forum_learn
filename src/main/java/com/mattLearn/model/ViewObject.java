package com.mattLearn.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Modified by Matt on Jan.14th 2021
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }
    public Object get(String key) {
        return objs.get(key);
    }
}
