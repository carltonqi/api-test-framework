package com.merico.inftest.datasets;

import com.google.common.collect.Lists;
import com.merico.inftest.cases.*;
import com.merico.inftest.commonutils.VarReplaceUtils;
import com.merico.inftest.commonutils.deepcopy.DeepCopyUtils;
import com.merico.inftest.context.Context;

import java.util.List;
import java.util.Map;

public class DatasetsUtils {

    public static List<TestCaseInfoNew> generateDataSetsCases(List<TestCaseInfoNew> cases) {

        List<TestCaseInfoNew> finalCases = Lists.newArrayList();
        for (TestCaseInfoNew testCaseInfoNew : cases) {

            if (testCaseInfoNew.getDatasets() == null || testCaseInfoNew.getDatasets().isEmpty()) {
                finalCases.add(testCaseInfoNew);
                continue;
            }
            finalCases.addAll(generateDatasetCase(testCaseInfoNew));
        }
        return finalCases;
    }


    private static List<TestCaseInfoNew> generateDatasetCase(TestCaseInfoNew testCaseInfo) {

        List<TestCaseInfoNew> clonedTestCases = Lists.newArrayList();

        List<Map<String, Object>> datasets = testCaseInfo.getDatasets();
        int count = 0;
        for (Map<String, Object> dataset : datasets) {
            TestCaseInfoNew clone = (TestCaseInfoNew) DeepCopyUtils.copy(testCaseInfo);
            BeforeOrAfterInfo beforeCase = clone.getBeforeCase();
            handleBeforeOrAfter(beforeCase, dataset);

            List<RequestInfoAndAssert> requests = clone.getRequests();
            if (null != requests && !requests.isEmpty()) {
                //处理request
                handleRequest(requests, dataset);
            }

            List<DBAssertInfo> dbAssertInfos = clone.getDbAssertInfos();
            if (dbAssertInfos != null && !dbAssertInfos.isEmpty()) {
                //处理dbAssertInfos
                handleDbAssertInfos(dbAssertInfos, dataset);
            }

            BeforeOrAfterInfo afterCase = clone.getAfterCase();
            handleBeforeOrAfter(afterCase, dataset);

            clone.setId(clone.getId() + "_" + count);
            clonedTestCases.add(clone);
            count++;

        }
        return clonedTestCases;


    }

    private static void handleDbAssertInfos(List<DBAssertInfo> dbAssertInfos, Map<String, Object> dataset) {
        for (DBAssertInfo dbAssertInfo : dbAssertInfos) {
            Context contextTmp = new Context();
            contextTmp.setContext(dataset);
            VarReplaceUtils.replaceDbAssertInfo(dbAssertInfo, contextTmp);
            VarReplaceUtils.replaceList(dbAssertInfo.getPlaceHolder(),contextTmp);
        }

    }

    private static void handleRequest(List<RequestInfoAndAssert> requestInfoAndAsserts, Map<String, Object> dataset) {
        Context contextTmp = new Context(dataset);
        requestInfoAndAsserts.forEach(
                requestInfoAndAssert -> {
                    VarReplaceUtils.replaceLocalCallInfo(requestInfoAndAssert.getLocalCallInfo(),contextTmp);
                    VarReplaceUtils.replaceRequestInfo(requestInfoAndAssert.getRequestInfo(), contextTmp);
                    VarReplaceUtils.replaceList(requestInfoAndAssert.getPlaceHolder(), contextTmp);
                    VarReplaceUtils.replaceAssertInfo(requestInfoAndAssert.getAssertInfo(), contextTmp);
                    //添加替换RequestInfoAndAssert.loopCheckInfo.requestInfo逻辑
                    VarReplaceUtils.replaceRequestInfo(requestInfoAndAssert.getLoopCheckInfo() != null? requestInfoAndAssert.getLoopCheckInfo().getRequestInfo() : null, contextTmp);
                }
        );
    }

    private static void handleBeforeOrAfter(BeforeOrAfterInfo beforeOrAfterInfo, Map<String, Object> dataset) {
        if (beforeOrAfterInfo != null) {
            List<RequestInfoAndAssert> requests = beforeOrAfterInfo.getRequests();
            if (requests != null && !requests.isEmpty()) {
                //处理request
                handleRequest(requests, dataset);
            }
            List<DBAssertInfo> dbAssertInfos = beforeOrAfterInfo.getDbAssertInfos();
            if (dbAssertInfos != null && !dbAssertInfos.isEmpty()) {
                //处理dbAssertInfos
                handleDbAssertInfos(dbAssertInfos, dataset);

            }

        }
    }


}
