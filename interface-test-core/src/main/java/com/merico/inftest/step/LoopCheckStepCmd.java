package com.merico.inftest.step;

import com.merico.inftest.cases.*;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

import java.util.List;
import java.util.Map;

public class LoopCheckStepCmd extends StepCmd {

    LoopCheckInfo loopCheckInfo;

    //单位 ms
    private int sleepTime = 30000;

    private int intervalTime = 1000;

    private Boolean breaked = true;

    public LoopCheckStepCmd(LoopCheckInfo loopCheckInfo) {
        this.loopCheckInfo = loopCheckInfo;
        this.breaked = loopCheckInfo.getBreaked() == null ? true : loopCheckInfo.getBreaked();
    }

    @Override
    public Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        Boolean flag = false;

        if (null != loopCheckInfo.getLoopTimeout() && loopCheckInfo.getLoopTimeout() >= 1000) {
            sleepTime = loopCheckInfo.getLoopTimeout();
        }

        if (null != loopCheckInfo.getIntervalTime() && loopCheckInfo.getIntervalTime() > 0) {
            intervalTime = loopCheckInfo.getIntervalTime();
        }

        int loopCount = sleepTime / intervalTime;

        for (int i = 0; i < loopCount; i++) {
            RequestInfo request = loopCheckInfo.getRequestInfo();
            if (null != request) {
                RequestStepCmd requestStepCmd = new RequestStepCmd(request);
                preResponse = requestStepCmd.doExcute(caseInfo, preResponse, context);
            } else if (null != loopCheckInfo.getLocalCallInfo()) {
                LocalCallStepCmd localCallStepCmd = new LocalCallStepCmd(loopCheckInfo.getLocalCallInfo());
                preResponse = localCallStepCmd.doExcute(caseInfo, preResponse, context);
            }


            AssertInfo assertInfo = loopCheckInfo.getAssertInfo();
            Map<String, Object> expect = assertInfo.buildExpectMap();

            if (i + 1 == loopCount) {
                flag = true;
            }

            Boolean assertBool = preResponse.verifyBool(expect, flag, breaked);

            if (assertBool) {
                return preResponse;
            } else {
                logger.warn("loop check: " + (i + 1) + " time, Retry after " + intervalTime + "ms.");
                Thread.sleep(intervalTime);
            }
        }

        return preResponse;
    }

    public LoopCheckInfo getLoopCheckInfo() {
        return loopCheckInfo;
    }

    public void setLoopCheckInfo(LoopCheckInfo loopCheckInfo) {
        this.loopCheckInfo = loopCheckInfo;
    }
}
