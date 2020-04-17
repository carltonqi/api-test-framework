package com.merico.inftest.http;

import static org.apache.commons.codec.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.commonutils.PropertyUtils;
import com.merico.inftest.commonutils.UrlUtils;
import com.merico.inftest.response.HttpResponse;
import com.merico.inftest.response.Response;

public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    private static CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(15 * 1000)
            .setSocketTimeout(300 * 1000)
            .build())
            .setRetryHandler((exception, executionCount, context) -> {
                if (executionCount > 3) {
                    LOGGER.warn("Maximum tries reached for client http pool ");
                    return false;
                }
                if (exception instanceof org.apache.http.NoHttpResponseException) {
                    LOGGER.warn("No response from server on " + executionCount + " call");
                    return true;
                }
                return false;
            }).build();


    private static HttpUriRequest getPostHttpRequest(String url, Map<String, Object> headers, String json) throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {
    	URL strUrl = new URL(url);
    	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
        HttpPost httpPost = new HttpPost(uri);
        if (headers != null && headers.containsKey("Content-Type") && headers.get("Content-Type").equals("application/x-www-form-urlencoded")) {
            Map<String, String> form = (Map) JSON.parse(json);
            if (form != null | form.size() >= 1) {
                List<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
                for (String pKey : form.keySet()) {
                    ps.add(new BasicNameValuePair(pKey, form.get(pKey)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(ps, "UTF-8"));
            }
            return httpPost;
        }

        if (headers != null && headers.containsKey("Content-Type") && headers.get("Content-Type").equals("multipart/form-data")) {

            headers.remove("Content-Type");
            Map<String, ContentBody> formdata = new LinkedHashMap<String, ContentBody>();
            JSONObject object = JSON.parseObject(json);
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                if (entry.getKey() != null && entry.getKey().contains("fileBody") && entry.getKey().equals("fileBody")) {
                    for (Map.Entry<String, Object> entryFileBody : JSON.parseObject(entry.getValue().toString()).entrySet()) {
                        formdata.put(entryFileBody.getKey(), new FileBody(new File(entryFileBody.getValue().toString())));
                    }
                } else if (entry.getKey() != null && entry.getKey().contains("stringBody") && entry.getKey().equals("stringBody")) {
                    for (Map.Entry<String, Object> entryStringBody : JSON.parseObject(entry.getValue().toString()).entrySet()) {
                        formdata.put(entryStringBody.getKey(), new StringBody(entryStringBody.getValue().toString(), ContentType.MULTIPART_FORM_DATA));
                    }
                }

            }

            if (formdata != null | formdata.size() >= 1) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (Map.Entry<String, ContentBody> param : formdata.entrySet()) {
                    multipartEntityBuilder.addPart(param.getKey(), param.getValue());
                }
                HttpEntity reqEntity = multipartEntityBuilder.build();
                httpPost.setEntity(reqEntity);
            }
            return httpPost;

        }


        if (StringUtils.isNotBlank(json)) {
            StringEntity stringEntity = new StringEntity(json, Charset.defaultCharset());
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
        return httpPost;


    }


    private static HttpUriRequest getGetHttpRequest(String url) throws URISyntaxException, MalformedURLException {
    	URL strUrl = new URL(url);
    	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
        HttpGet httpGet = new HttpGet(uri);
        return httpGet;
    }

    private static HttpUriRequest getPutHttpRequest(String url, String json) throws MalformedURLException, URISyntaxException {
    	URL strUrl = new URL(url);
    	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
        HttpPut httpPut = new HttpPut(uri);
        if (StringUtils.isNotBlank(json)) {
            StringEntity stringEntity = new StringEntity(json, Charset.defaultCharset());
            stringEntity.setContentType("application/json");
            httpPut.setEntity(stringEntity);
        }
        return httpPut;
    }

    private static HttpUriRequest getPatchHttpRequest(String url, String json) throws MalformedURLException, URISyntaxException {
    	URL strUrl = new URL(url);
    	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
        HttpPatch httpPatch = new HttpPatch(uri);
        if (StringUtils.isNotBlank(json)) {
            StringEntity stringEntity = new StringEntity(json, Charset.defaultCharset());
            stringEntity.setContentType("application/json");
            httpPatch.setEntity(stringEntity);
        }
        return httpPatch;
    }

    private static HttpUriRequest getDelete(String url, String entity) throws MalformedURLException, URISyntaxException {
    	URL strUrl = new URL(url);
    	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
    	
        if (!StringUtils.isNotBlank(entity)) {
            HttpDelete httpDelete = new HttpDelete(uri);
            return httpDelete;
        } else {
            HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(uri);
            StringEntity stringEntity = new StringEntity(entity, Charset.defaultCharset());
            stringEntity.setContentType("application/json");
            httpDeleteWithBody.setEntity(stringEntity);
            return httpDeleteWithBody;
        }

    }


    public static Response execute(RequestInfo requestInfo) throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {

        //获取对应 HttpUriRequest post只考虑 post json情况 不考虑表单提交
        String url = UrlUtils.generateUrl(requestInfo.getUrl(), requestInfo.getParams());
        Map<String, Object> headers = requestInfo.getHeaders();

        HttpUriRequest request = getRequest(url, requestInfo.getMethod(), headers, requestInfo.getEntity());

        //统一处理header
        if (null != headers && !headers.isEmpty()) {
            if (headers.containsKey("access_token")||headers.containsKey("access-token")) {
                headers.put("access-token", PropertyUtils.getAccessToken());
            }

            //测试用例的headers中如果包含"dynamicCookie"标签，则往headers中塞入动态值
            if (headers.containsKey("dynamicCookie")) {
                PropertyUtils.setDynamicCookie();
                headers.put("Cookie", PropertyUtils.getDynamicCookie());
                headers.remove("dynamicCookie");
            }

            headers.forEach((k, v) -> {
                if (null != v && null != k) {
                    request.setHeader(k, v.toString());
                }
            });
        }

        Reporter.log("<b>REQUEST:</b> "+requestInfo.getMethod()+"  "+" request step begin. url: " +url+" parameters:  "+requestInfo.getEntity()+"\n");
        LOGGER.info("{} request step begin. url:{}  {} ", requestInfo.getMethod(), url, requestInfo.getEntity());
        //发送请求
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = httpClient.execute(request);
        } catch (IOException e) {
            LOGGER.error("request failed！url:{} ", url, e);
            Reporter.log("request failed！url: "+url+"  "+e.toString());
            throw new RuntimeException("request failed！url:" + url);
        }

        //结果返回封装 Response
        if (null == closeableHttpResponse) {
            throw new RuntimeException("request failed. No response！url:" + url);
        }
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        String responseBody = null;
        Header[] reponseHeaders = null;
        try {
            responseBody = EntityUtils.toString(closeableHttpResponse.getEntity(), UTF_8);
            reponseHeaders = closeableHttpResponse.getAllHeaders();
        } catch (IOException e) {
            LOGGER.error("获取结果失败! url:{}", url, e);
            Reporter.log("获取结果失败! url: "+url+"  "+ e.toString()+"\n");
        } finally {
            try {
                closeableHttpResponse.close();
            } catch (IOException e) {
                Reporter.log("close response proxy error "+e.toString()+"\n");
                LOGGER.error("close response proxy error", e);
            }
        }

        Reporter.log("<b>RESPONSE:</b> "+requestInfo.getMethod()+" request step success. url: "+ url +" , status: "+statusCode+ ", response: "+ responseBody);
        LOGGER.info("{} request step success. url:{} , status:{}, response:{} ", requestInfo.getMethod(), url, statusCode, responseBody);
        return new HttpResponse(responseBody, statusCode, reponseHeaders);
    }


    private static HttpUriRequest getRequest(String url, String method, Map<String, Object> headers, String entity) throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {
        switch (method.toLowerCase()) {
            case "get":
                return getGetHttpRequest(url);
            case "post":
                return getPostHttpRequest(url, headers, entity);
            case "put":
                return getPutHttpRequest(url, entity);
            case "patch":
                return getPatchHttpRequest(url,entity);
            case "delete":
                return getDelete(url, entity);
            default:
                LOGGER.error("unsupport http request method!" + method);
                throw new RuntimeException("unsupport http request method!" + method);
        }
    }

}
