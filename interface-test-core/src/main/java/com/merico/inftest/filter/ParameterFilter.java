package com.merico.inftest.filter;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.cases.RequestInfo;

public abstract class ParameterFilter {


    public abstract boolean isSupport(RequestInfo requestInfo);

    public abstract void doFilter(RequestInfo requestInfo);

    public void filter(RequestInfo requestInfo){
        if(isSupport(requestInfo)){
            doFilter(requestInfo);
        }
    }

}
