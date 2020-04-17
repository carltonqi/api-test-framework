package com.merico.inftest.step;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.context.Context;
import com.merico.inftest.filter.ParameterFilterFactory;
import com.merico.inftest.http.HttpClientUtils;
import com.merico.inftest.response.Response;

public class RequestStepCmd extends StepCmd {

    private RequestInfo requestInfo;

    public RequestStepCmd() {
    }

    public RequestStepCmd(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    @Override
    public Response doExcute(CaseInfo CaseInfo, Response preResponse, Context context) throws Throwable {

        ParameterFilterFactory.filte(requestInfo);

        Response response = null;
        //判断 thrift 接口
        if (requestInfo.getUrl().startsWith("thrift:") ) {
//            response = ThriftClientUtils.excute(this.requestInfo);

        } else if (requestInfo.getUrl().startsWith("pigeon:")) {
//            response = PigeonClientUtils.excute(this.requestInfo);
        }else {
            response = HttpClientUtils.execute(this.requestInfo);
        }
        logger.info("requestStepCmd response:{}", response.getBody());
        return response;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }
}
