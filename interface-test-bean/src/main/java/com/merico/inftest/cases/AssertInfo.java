package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssertInfo implements CaseInfo, Serializable {

    @JSONField(ordinal = 1)
    private Integer statusCode;

    @JSONField(ordinal = 2, name = "check")
    private Map<String, Object> body;

    @JSONField(ordinal = 3)
    private List<String> keyExist;

    @JSONField(ordinal = 4)
    private List<String> exclude;

    @JSONField(ordinal = 5)
    private Map<String, List<Object>> failed;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public List<String> getKeyExist() {
        return keyExist;
    }

    public void setKeyExist(List<String> keyExist) {
        this.keyExist = keyExist;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public Map<String, List<Object>> getFailed() {
        return failed;
    }

    public void setFailed(Map<String, List<Object>> failed) {
        this.failed = failed;
    }

    public Map<String, Object> buildExpectMap() {

        Map<String, Object> expectMap = Maps.newHashMap();
        expectMap.put("statusCode", statusCode);
        if (body != null) {
            expectMap.put("body", body);
        }
        if (keyExist != null && keyExist.size() != 0) {
            expectMap.put("keyExist", keyExist);
        }
        if (exclude != null && exclude.size() != 0) {
            expectMap.put("exclude", exclude);
        }
        if (failed != null && failed.size() != 0) {
            expectMap.put("failed", failed);
        }
        return expectMap;
    }


}
