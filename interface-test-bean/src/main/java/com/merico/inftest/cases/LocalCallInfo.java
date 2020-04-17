package com.merico.inftest.cases;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;
import java.util.Map;

public class LocalCallInfo implements CaseInfo, Serializable {

    private static final long serialVersionUID = 5567530705754709392L;

    @JSONField(ordinal = 1)
    private String factory;

    @JSONField(ordinal = 2)
    private String factoryMethod;

    @JSONField(ordinal = 3)
    private String method;

    @JSONField(ordinal = 4)
    private String serviceInterface;

    @JSONField(ordinal = 5)
    private Map<String, Object> params;

    @JSONField(ordinal = 6)
    private Map<String, Object> conParams;

    public String getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getConParams() {
        return conParams;
    }

    public void setConParams(Map<String, Object> conParams) {
        this.conParams = conParams;
    }

}
