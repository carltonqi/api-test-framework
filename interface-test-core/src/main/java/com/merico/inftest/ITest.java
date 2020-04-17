package com.merico.inftest;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.commonutils.ReaderFileUtils;
import com.merico.inftest.commonutils.TestCasesFiles;
import com.merico.inftest.context.Context;
import com.merico.inftest.filter.CaseTagsFilters;
import com.merico.inftest.intercept.InterceptFactory;
import com.merico.inftest.response.Response;
import com.merico.inftest.step.StepCmd;
import com.merico.inftest.step.StepFactory;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

public class ITest {


    //测试方法主类 执行过程如下:

    @Parameters({"caseFile", "tags"})
    @Test
    public void iTest(String caseFile, String tags) throws Throwable {
        //根据caseFile 找到caseFile path
        List<String> testCasesFiles = TestCasesFiles.getTestCasesFiles(caseFile);
        Preconditions.checkArgument(!testCasesFiles.isEmpty(), "未找到case文件，请检查case文件目录！");

        //对于其中的一个文件 执行如下流程
        for (String testCasePath : testCasesFiles) {

            //1.0 解析 json 文件生成 TestcaseInfo
            String fileContext = ReaderFileUtils.getFileContext(testCasePath);
            List<TestCaseInfoNew> testCases = JSON.parseArray(fileContext, TestCaseInfoNew.class);

            //1.1 若tag不为空执行tags 过滤
            new CaseTagsFilters(tags).filter(testCases);

            Preconditions.checkArgument(!testCases.isEmpty(), "未找到符合条件的tags，请检查case文件:" + testCasePath);

            //1.2 对于每个TestCaseInfo 执行拦截器 主要是替换url中的变量
            testCases.forEach(testCaseInfo -> InterceptFactory.beforStep(testCaseInfo, new Response()));

            //1.3 case执行 记录相关信息和结果
            for(TestCaseInfoNew testCaseInfo : testCases){
                StepCmd stepCmd = StepFactory.generateStepCmd(testCaseInfo);
                stepCmd.excute(testCaseInfo,null,new Context());
            }
        }
        //生成测试报告 ？？ 使用ExtentReport http://extentreports.com/docs/versions/3/java/#create-logs


    }

}
