package com.merico.inftest.commonutils;

public class KeyValueStore {

    private String key;

    private Object Value;

    public KeyValueStore(String key, Object value) {
        this.key = key;
        Value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }

}
