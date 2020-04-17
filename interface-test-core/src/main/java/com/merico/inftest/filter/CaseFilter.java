package com.merico.inftest.filter;

import java.util.List;

import com.merico.inftest.cases.TestCaseInfoNew;

public interface CaseFilter {

    void filter(List<TestCaseInfoNew> testCaseInfos);
}
