package com.merico.inftest.step;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.commonutils.VarReplaceUtils;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StepCmd {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private StepCmd nextStepCmd;


    public StepCmd getNextStepCmd() {
        return nextStepCmd;
    }

    public void setNextStepCmd(StepCmd nextStepCmd) {
        this.nextStepCmd = nextStepCmd;
    }

    /**
     * step 执行方法 所有的step 通过此方法执行 自定义的step 只需要通过 实现doexcute 即可 实现自己的step
     *
     * @param caseInfo
     * @param preResponse
     * @throws Throwable 每个step执行失败 会抛出异常 中断执行
     */
    public void excute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        logger.info("{} step begin.", this.getClass().getName());
        //执行变量替换
        VarReplaceUtils.varReplace(this, context);

        Response response = null;
        try {
            response = doExcute(caseInfo, preResponse, context);
        } catch (Throwable e) {
            logger.error("step execte faild! step name {}", getClass().getName());

            throw e;
        }
        logger.info("{} step end.", this.getClass().getName());
        excuteNextStep(caseInfo, response, context);

    }

    private void excuteNextStep(CaseInfo caseInfo, Response response, Context context) throws Throwable {
        if (null != nextStepCmd) {
            nextStepCmd.excute(caseInfo, response, context);
        }
    }

    public abstract Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable;

}
