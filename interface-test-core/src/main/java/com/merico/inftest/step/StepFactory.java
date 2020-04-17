package com.merico.inftest.step;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import com.merico.inftest.cases.*;

import java.util.List;

public class StepFactory {

    private static Logger logger = LoggerFactory.getLogger(StepFactory.class);

    public static StepCmd generateStepCmd(StepCaseInfo testCaseInfo) {
        return generateStepCmd(testCaseInfo, ((TestCaseInfoNew) testCaseInfo).getDatabase());
    }

    public static StepCmd generateStepCmd(StepCaseInfo testCaseInfo, String dbName) {
        List<StepCmd> cmdList = Lists.newArrayList();

        List<RequestInfoAndAssert> requests = testCaseInfo.getRequests();
        if (null != requests && !requests.isEmpty()) {
            for (RequestInfoAndAssert requestInfoAndAssert : testCaseInfo.getRequests()) {

                String templatePath = requestInfoAndAssert.getTemplatePath();
                if(StringUtils.isNotBlank(templatePath)){
                    List<StepCmd> templateStep = TemplateStepGenerate.generateTempStep(templatePath, dbName);
                    cmdList.addAll(templateStep);
                    continue;
                }
                generateRequestInfoAndAssert(dbName, cmdList, requestInfoAndAssert);
            }
        }

        //dbassert可以放入到request后  此处暂不删除兼容之前的代码
        List<DBAssertInfo> dbAssertInfos = testCaseInfo.getDbAssertInfos();
        generateDbAssertStepCmds(dbName, cmdList, dbAssertInfos);

        //cmd组装连接
        for (int i = 0; i < cmdList.size() - 1; i++) {
            cmdList.get(i).setNextStepCmd(cmdList.get(i + 1));
        }

        return cmdList.get(0);

    }

    public static void generateRequestInfoAndAssert(String dbName, List<StepCmd> cmdList, RequestInfoAndAssert requestInfoAndAssert) {
        LocalCallInfo localCallInfo = requestInfoAndAssert.getLocalCallInfo();
        RequestInfo request = requestInfoAndAssert.getRequestInfo();

        //本地jar包调用 还是请求只能有一个
        if (null != localCallInfo) {
            LocalCallStepCmd localCallCmd = new LocalCallStepCmd(localCallInfo);
            cmdList.add(localCallCmd);
        } else if (null != request) {
            RequestStepCmd requestStepCmd = new RequestStepCmd(request);
            cmdList.add(requestStepCmd);
        }

        List<String> placeHolder = requestInfoAndAssert.getPlaceHolder();
        if (placeHolder != null && placeHolder.size() != 0) {
            PlaceHolderStepCmd placeHolderStepCmd = new PlaceHolderStepCmd(placeHolder);
            cmdList.add(placeHolderStepCmd);
        }

        if (null != requestInfoAndAssert.getWaitTime() && requestInfoAndAssert.getWaitTime() != 0) {
            SleepStepCmd sleepStepCmd = new SleepStepCmd(requestInfoAndAssert.getWaitTime());
            cmdList.add(sleepStepCmd);
        }

        AssertInfo assertInfo = requestInfoAndAssert.getAssertInfo();
        if (null != assertInfo) {
            AssertStepCmd assertStepCmd = new AssertStepCmd(assertInfo);
            cmdList.add(assertStepCmd);
        }

        List<DBAssertInfo> dbAssertInfo = requestInfoAndAssert.getDbAssertInfos();
        generateDbAssertStepCmds(dbName, cmdList, dbAssertInfo);

        LoopCheckInfo loopCheckInfo = requestInfoAndAssert.getLoopCheckInfo();
        if (null != loopCheckInfo) {
            LoopCheckStepCmd loopCheckStepCmd = new LoopCheckStepCmd(loopCheckInfo);
            cmdList.add(loopCheckStepCmd);
        }
    }

    private static void generateDbAssertStepCmds(String dbName, List<StepCmd> cmdList, List<DBAssertInfo> dbAssertInfos) {
        if (dbAssertInfos != null && !dbAssertInfos.isEmpty()) {
            dbAssertInfos.forEach(dbAssertInfo -> generateDbAssertStepCmd(dbName, cmdList, dbAssertInfo));
        }
    }

    private static void generateDbAssertStepCmd(String dbName, List<StepCmd> cmdList, DBAssertInfo dbAssertInfo) {
        if (null != dbAssertInfo) {

            if (StringUtils.isNotBlank(dbAssertInfo.getSql())) {
                SqlExecStepCmd sqlExecStepCmd = new SqlExecStepCmd(dbName, dbAssertInfo.getSql());
                cmdList.add(sqlExecStepCmd);
            }

            List<String> placeHolder = dbAssertInfo.getPlaceHolder();
            if (placeHolder != null && placeHolder.size() != 0) {
                PlaceHolderStepCmd placeHolderStepCmd = new PlaceHolderStepCmd(placeHolder);
                cmdList.add(placeHolderStepCmd);
            }

            if (dbAssertInfo.getBody() != null && !dbAssertInfo.getBody().isEmpty()) {
                AssertInfo assertInfoTmp = new AssertInfo();
                assertInfoTmp.setBody(dbAssertInfo.getBody());
                AssertStepCmd assertStepCmd = new AssertStepCmd(assertInfoTmp);
                cmdList.add(assertStepCmd);
            }
        }
    }
}

