package com.merico.inftest.step;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

public class SleepStepCmd extends StepCmd {
    //单位 ms
    private int waitTime;


    public SleepStepCmd(int waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        logger.info("{} step start! wait time: {} ms ",this.getClass().getName(), waitTime);
        Thread.sleep(waitTime);
        logger.info("{} step end!",this.getClass().getName());
        return preResponse;
    }
}
