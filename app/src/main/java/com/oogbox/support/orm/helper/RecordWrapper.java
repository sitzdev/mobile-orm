package com.oogbox.support.orm.helper;

import java.util.HashMap;

public abstract class RecordWrapper<T> extends HashMap<String, Object> {

    public Boolean getBoolean(String key) {
        if (isValid(key)) {
            return Boolean.valueOf(getString(key));
        }
        return null;
    }

    public Float getFloat(String key) {
        if (isValid(key)) {
            return Float.valueOf(getString(key));
        }
        return null;
    }

    public Integer getInt(String key) {
        if (isValid(key)) {
            return Integer.valueOf(getString(key));
        }
        return null;
    }

    public String getString(String key) {
        if (isValid(key)) {
            return get(key).toString();
        }
        return null;
    }

    public boolean isValid(String key) {
        return containsKey(key) && get(key) != null;
    }
}
