package com.github.sadcenter.impl.utils;

import java.util.HashMap;
import java.util.Map;

public class HashMapBuilder {

    private final Map<String, Object> map = new HashMap<>();

    public HashMapBuilder withParam(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }
}
