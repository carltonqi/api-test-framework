package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RequestInfoAndAssert implements Serializable {


    @JSONField(ordinal = 1, name = "template")
    private String templatePath = "";

    @JSONField(ordinal = 2, name = "localcall")
    private LocalCallInfo localCallInfo;

    @JSONField(ordinal = 3, name = "request")
    private RequestInfo requestInfo;

    @JSONField(ordinal = 4, name = "loopCheck")
    private LoopCheckInfo loopCheckInfo;

    @JSONField(ordinal = 5, name = "placeholder")
    private List<String> placeHolder;

    @JSONField(ordinal = 6, name = "waitTime")
    private Integer waitTime;

    @JSONField(ordinal = 7, name = "assert")
    private AssertInfo assertInfo;

    @JSONField(ordinal = 8, name = "dbassert")
    private List<DBAssertInfo> dbAssertInfos;



}
