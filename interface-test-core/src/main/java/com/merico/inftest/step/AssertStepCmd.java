package com.merico.inftest.step;

import com.merico.inftest.cases.AssertInfo;
import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

import java.util.Map;

public class AssertStepCmd extends StepCmd{
    AssertInfo assertInfo;

    public AssertStepCmd(AssertInfo assertInfo) {
        this.assertInfo = assertInfo;
    }

    @Override
    public Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        Map<String, Object> expect = assertInfo.buildExpectMap();
        preResponse.verify(expect);
        return preResponse;
    }

    public AssertInfo getAssertInfo() {
        return assertInfo;
    }

    public void setAssertInfo(AssertInfo assertInfo) {
        this.assertInfo = assertInfo;
    }
}
