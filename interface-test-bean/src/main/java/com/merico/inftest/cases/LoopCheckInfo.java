package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

public class LoopCheckInfo implements CaseInfo, Serializable {

    @JSONField(ordinal = 1)
    private Integer loopTimeout;

    @JSONField(ordinal = 2)
    private Integer intervalTime;

    @JSONField(ordinal = 3)
    private Boolean breaked;

    @JSONField(ordinal = 4, name = "request")
    private RequestInfo requestInfo;

    @JSONField(ordinal = 5, name = "localcall")
    private LocalCallInfo localCallInfo;

    @JSONField(ordinal = 6, name = "checkStatus")
    private AssertInfo assertInfo;

    public Integer getLoopTimeout() {
        return loopTimeout;
    }

    public void setLoopTimeout(Integer loopTimeout) {
        this.loopTimeout = loopTimeout;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public AssertInfo getAssertInfo() {
        return assertInfo;
    }

    public void setAssertInfo(AssertInfo assertInfo) {
        this.assertInfo = assertInfo;
    }

    public Boolean getBreaked() {
        return breaked;
    }

    public void setBreaked(Boolean breaked) {
        this.breaked = breaked;
    }

    public LocalCallInfo getLocalCallInfo() {
        return localCallInfo;
    }

    public void setLocalCallInfo(LocalCallInfo localCallInfo) {
        this.localCallInfo = localCallInfo;
    }
}
