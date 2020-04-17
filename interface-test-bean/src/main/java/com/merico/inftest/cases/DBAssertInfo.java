package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DBAssertInfo implements CaseInfo, Serializable {

    @JSONField(ordinal = 1)
    private String sql;

    @JSONField(ordinal = 2, name = "placeholder")
    private List<String> placeHolder;

    @JSONField(ordinal = 3)
    private Map<String, Object> body;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public List<String> getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(List<String> placeHolder) {
        this.placeHolder = placeHolder;
    }

    public Map<String, Object> buildExpectMap() {
        Map<String, Object> expectMap = Maps.newHashMap();
        if (body != null && body.size() != 0) {
            expectMap.put("body", body);
        }
        return expectMap;
    }
}
