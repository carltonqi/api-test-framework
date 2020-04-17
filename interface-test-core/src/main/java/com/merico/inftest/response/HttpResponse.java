package com.merico.inftest.response;

import org.apache.http.Header;
import org.testng.Assert;

import java.util.Map;

public class HttpResponse extends Response {
    private Integer statusCode;

    private Header[] headers;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public HttpResponse(String body, int statusCode, Header[] headers) {
        super(body);
        this.statusCode = statusCode;
        this.headers = headers;
    }


    @Override
    public void verify(Map<String, Object> expect) {
        assertStatusCode(expect.get("statusCode"));
        expect.remove("statusCode"); //如果通过从返回值移除statusCode
        super.verify(expect);
    }

    private void assertStatusCode(Object expectStatusCode) {
        Assert.assertEquals(this.statusCode, expectStatusCode, "statusCode");

    }
}
