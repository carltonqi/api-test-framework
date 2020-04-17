package com.merico.inftest.commonutils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.http.HttpClientUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SSOLoginUtils {

    public static  String ssoLogin(String clientId, String clientSecret, String mis, String pwd, String env) throws UnsupportedEncodingException, JsonSyntaxException, MalformedURLException, URISyntaxException {
        RequestInfo requestInfoToken = new RequestInfo();

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        String url = "/api/v1/sso/get/ssoid";
        String ssoLoginHost =  PropertyUtils.getProperty("ssoLoginHost");

        requestInfoToken.setUrl(ssoLoginHost + url);
        requestInfoToken.setMethod("POST");
        requestInfoToken.setEntity("{\"env\":" + "\"" + env + "\"" + ",\"mis\":" + "\"" + mis + "\"" + ",\"pwd\":" + "\"" + pwd + "\"" + ",\"clientId\":" + "\"" + clientId + "\"" +",\"secret\":" + "\"" + clientSecret + "\"" + "}");
        map = gson.fromJson(HttpClientUtils.execute(requestInfoToken).getBody(), map.getClass());
        String accessToken = map.get("data").toString();

        return accessToken;
    }
}
