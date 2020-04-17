package com.merico.inftest.step;

import com.alibaba.fastjson.JSON;
import com.merico.inftest.cases.RequestInfoAndAssert;
import com.merico.inftest.commonutils.ReaderFileUtils;

import org.testng.collections.Lists;

import java.util.List;

public class TemplateStepGenerate {


    public static List<StepCmd> generateTempStep(String templatePath, String dbName) {

        List<StepCmd> stepCmdList = Lists.newArrayList();
        // 根据template 读取内容
        String filePath = ReaderFileUtils.getFilePath(templatePath);
        String fileContext = ReaderFileUtils.getFileContext(filePath);

        // 根据内容 序列化为 List<RequestInfoAndAssert>
        List<RequestInfoAndAssert> requestInfoAndAsserts = JSON.parseArray(fileContext, RequestInfoAndAssert.class);

        //对 List<RequestInfoAndAssert>  序列化翻译
        requestInfoAndAsserts.forEach(requestInfoAndAssert -> StepFactory.generateRequestInfoAndAssert(dbName, stepCmdList, requestInfoAndAssert));

        return stepCmdList;
    }


}
