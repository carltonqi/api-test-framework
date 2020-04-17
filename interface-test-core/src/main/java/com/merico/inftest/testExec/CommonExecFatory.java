package com.merico.inftest.testExec;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.commonutils.PropertyUtils;
import com.merico.inftest.commonutils.ReaderFileUtils;
import com.merico.inftest.commonutils.ReflectUtils;
import com.merico.inftest.datasets.DatasetsUtils;
import com.merico.inftest.filter.CaseTagsFilters;
import com.merico.inftest.filter.ParameterFilter;
import com.merico.inftest.filter.ParameterFilterFactory;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

public class CommonExecFatory {

    @Parameters({"filenames", "tags", "paramFilters", "clientId", "clientSecret", "user", "password", "env"})
    @Factory
    public Object[] createInstance(String filenames, String tags, @Optional String paramFilters,
                                   @Optional String clientId, @Optional String clientSecret,
                                   @Optional String user, @Optional String password, @Optional String env) throws UnsupportedEncodingException, JsonSyntaxException, MalformedURLException, URISyntaxException {
        //自定义paramFilter注册
        if (StringUtils.isNotBlank(paramFilters)) {
            List<String> filterList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(paramFilters);
            filterList.forEach(filterStr -> {
                ParameterFilter filteClass = (ParameterFilter) ReflectUtils.getClassFromName(filterStr);
                ParameterFilterFactory.addFilter(filteClass);
            });
        }

        if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)
                && StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(env)) {
            PropertyUtils.setAccessToken(clientId, clientSecret, user, password, env);
        }

        List<String> filesWithPaths = ReaderFileUtils.getFilesWithPath(filenames);

        if (filesWithPaths == null || filesWithPaths.isEmpty()) {
            throw new RuntimeException("未找到case文件!!请检查" + filenames);
        }
        List<TestCaseInfoNew> testCases = Lists.newArrayList();
        for (String filesWithPath : filesWithPaths) {
            if (!filesWithPath.endsWith(".json")) {
                //非json格式文件过滤
                continue;
            }
            String fileContext = ReaderFileUtils.getFileContext(filesWithPath);
            testCases.addAll(JSON.parseArray(fileContext, TestCaseInfoNew.class));
        }

        List<TestCaseInfoNew> finalTestCases = DatasetsUtils.generateDataSetsCases(testCases);
        //处理tags
        CaseTagsFilters caseTagsFilters = new CaseTagsFilters(tags);
        caseTagsFilters.filter(finalTestCases);

        if (null == finalTestCases || finalTestCases.isEmpty()) {
            throw new RuntimeException("未找到符合条件的case!");
        }

        List<Object> objects = Lists.newArrayList();
        for (TestCaseInfoNew testCase : finalTestCases) {
            objects.add(new CommonExec(filenames, testCase.getId(), testCase.getDesc(), testCase));
        }
        return objects.toArray();
    }
}