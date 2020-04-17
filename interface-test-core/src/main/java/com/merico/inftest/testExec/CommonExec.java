package com.merico.inftest.testExec;

import com.merico.inftest.cases.BeforeOrAfterInfo;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.context.Context;
import com.merico.inftest.intercept.InterceptFactory;
import com.merico.inftest.response.Response;
import com.merico.inftest.step.StepCmd;
import com.merico.inftest.step.StepFactory;

import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.annotations.*;

public class CommonExec implements ITest {

    Logger logger = LoggerFactory.getLogger(CommonExec.class);

    private String testCaseFileName;
    private String caseId;
    private String desc;
    private TestCaseInfoNew testCaseInfoNew;

    private Context context = new Context();

    public String getTestCaseFileName() {
        return testCaseFileName;
    }

    public void setTestCaseFileName(String testCaseFileName) {
        this.testCaseFileName = testCaseFileName;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TestCaseInfoNew getTestCaseInfo() {
        return testCaseInfoNew;
    }

    public void setTestCaseInfo(TestCaseInfoNew testCaseInfo) {
        this.testCaseInfoNew = testCaseInfo;
    }

    public CommonExec(String testCaseFileName, String caseId, String desc, TestCaseInfoNew testCaseInfo) {
        this.testCaseFileName = testCaseFileName;
        this.caseId = caseId;
        this.desc = desc;
        this.testCaseInfoNew = testCaseInfo;
    }

    @BeforeClass(alwaysRun = true)
    public void beforeCase() throws Throwable {
        logger.info("id: {} case begin",caseId);
        logger.info("before starting~~~~");
        BeforeOrAfterInfo beforeCase = testCaseInfoNew.getBeforeCase();
        if (null == beforeCase ||
                (beforeCase.getRequests() == null || beforeCase.getRequests().size() == 0)
                        && (null == beforeCase.getDbAssertInfos() || beforeCase.getDbAssertInfos().size() == 0)) {
            logger.info("~~~~~~~ no beforeCase!~~~~~~~~~~~~");
            return;
        }
        InterceptFactory.beforStep(beforeCase, new Response());
        StepCmd stepCmd = StepFactory.generateStepCmd(beforeCase, testCaseInfoNew.getDatabase());
        stepCmd.excute(beforeCase, null, context);
        logger.info("before ending~~~~");

    }

    @Test(alwaysRun = true)
    public void exect() throws Throwable {
        //主要处理url host 替换
        InterceptFactory.beforStep(testCaseInfoNew, new Response());
        StepCmd stepCmd = StepFactory.generateStepCmd(testCaseInfoNew);
        stepCmd.excute(testCaseInfoNew, null, context);
    }

    @AfterClass(alwaysRun = true)
    public void afterCase() throws Throwable {
        logger.info("after starting~~~~");
        BeforeOrAfterInfo afterCase = testCaseInfoNew.getAfterCase();
        if (null == afterCase ||
                (afterCase.getRequests() == null || afterCase.getRequests().size() == 0)
                        && (afterCase.getDbAssertInfos() == null || afterCase.getDbAssertInfos().size() == 0)) {
            logger.info("~~~~~~~ no afterCase!~~~~~~~~~~~~");
            return;
        }
        InterceptFactory.beforStep(afterCase, new Response());
        StepCmd stepCmd = StepFactory.generateStepCmd(afterCase, testCaseInfoNew.getDatabase());
        stepCmd.excute(afterCase, null, context);
        logger.info("after ending~~~~");
        logger.info("id: {} case end",caseId);


    }

    @Override
    public String getTestName() {
        return caseId + "";
    }
}
