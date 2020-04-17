package com.merico.inftest.filter;

import org.testng.collections.Lists;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.cases.RequestInfo;

import java.util.List;

public class ParameterFilterFactory {

    private static List<ParameterFilter> FILTERS = Lists.newArrayList();


    public static void addFilter(ParameterFilter parameterFilter){
        FILTERS.add(parameterFilter);
    }

    public static void filte(RequestInfo requestInfo){
        FILTERS.forEach(filter ->  filter.filter(requestInfo));
    }

}
