package com.merico.inftest.commonutils;

import com.google.common.base.Joiner;

import java.util.Map;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlUtils {

    static Logger logger = LoggerFactory.getLogger(UrlUtils.class);
    private static final String VARIABLE_REGEX = "^\\$\\{.*\\}$";

    /**
     * 获取变量名称
     *
     * @param Variable
     * @return
     */
    public static String getVarName(String Variable) {
        //变量形式 ${val}
        String varName = Variable.substring(Variable.indexOf("{") + 1, Variable.lastIndexOf("}"));
        return varName;

    }


    /**
     * string是否是变量
     *
     * @param string
     * @return
     */
    public static boolean isVariable(String string) {
        return string.matches(VARIABLE_REGEX);
    }

    /**
     * 拼接请求参数
     *
     * @param params
     * @return
     */
    public static String convertParams2Str(Map<String, Object> params) {
        if (null == params && params.isEmpty()) {
            return null;
        }

        for(Map.Entry<String, Object> entry : params.entrySet()){
            if(entry.getValue() instanceof JSONObject) {
                try {
                    String result = java.net.URLEncoder.encode(String.valueOf(entry.getValue()), "utf8");
                    params.put(entry.getKey(), result);
                } catch (UnsupportedEncodingException e) {
                    logger.error("url encode error!!!", e);
                }
            }
        }
        return Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

    public static String generateUrl(String originUrl, Map<String, Object> params) {
        if (null == params || params.isEmpty()) {
            return originUrl;
        }
        return originUrl + "?" + convertParams2Str(params);

    }
}
