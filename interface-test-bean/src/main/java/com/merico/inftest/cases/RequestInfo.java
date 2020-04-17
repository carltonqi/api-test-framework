package com.merico.inftest.cases;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;
import java.util.Map;

public class RequestInfo implements CaseInfo, Serializable {

    @JSONField(ordinal = 1)
    private String url;

    @JSONField(ordinal = 2)
    private String method;

    /**
     * pigeon 接口使用 serviceInterface
     */
    @JSONField(ordinal = 3)
    private String serviceInterface;

    /**
     * thrift 接口使用 appKey
     */
    @JSONField(ordinal = 4)
    private String appKey;

    /**
     * thrift 接口使用 remoteAppKey
     */
    @JSONField(ordinal = 5)
    private String remoteAppKey;

    /**
     * thrift 接口使用 remote server port
     */
    @JSONField(ordinal = 6)
    private String port;

    @JSONField(ordinal = 7)
    private String serverIpPorts;

    @JSONField(ordinal = 8)
    private Map<String, Object> params;

    @JSONField(ordinal = 9)
    private Map<String, Object> headers;

    // post请求时 请把post entity放入这个字段
    @JSONField(ordinal = 10)
    private String entity;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getRemoteAppKey() {
        return remoteAppKey;
    }

    public void setRemoteAppKey(String remoteAppKey) {
        this.remoteAppKey = remoteAppKey;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServerIpPorts() {
        return serverIpPorts;
    }

    public void setServerIpPorts(String serverIpPorts) {
        this.serverIpPorts = serverIpPorts;
    }

    @Override
    public String toString() {
        return super.toString(); //todo 后面有时间可以进一步优化
    }
}
