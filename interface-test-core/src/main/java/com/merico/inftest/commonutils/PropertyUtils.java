package com.merico.inftest.commonutils;

import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

public class PropertyUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtils.class);

    private static Map<String, String> configs = Maps.newHashMap();

    private static Map<String, String> caseConfigs = Maps.newHashMap();

    private static String token;

    private static String dynamicCookie;

    static {
        loadCaseConfig();
        loadConfig();
    }

    private static void loadConfig() {
        Properties properties = new Properties();

        InputStream resourceAsStream = PropertyUtils.class.getClassLoader().getResourceAsStream("config.properties");
        if (resourceAsStream == null) {
            return;
        }
        try {
            properties.load(resourceAsStream);
            properties.forEach((k, v) -> configs.put(k.toString().trim(), v.toString().trim()));
        } catch (IOException e) {
            LOGGER.error("loading config.properties failed!!", e);
        }
    }

    private static void loadCaseConfig(){
        Properties properties = new Properties();

        InputStream resourceAsStream = PropertyUtils.class.getClassLoader().getResourceAsStream("case_config.properties");
        if (resourceAsStream == null) {
            return;
        }
        try {
            properties.load(resourceAsStream);
            properties.forEach((k, v) -> caseConfigs.put(k.toString().trim(), v.toString().trim()));
        } catch (IOException e) {
            LOGGER.error("loading case_config.properties failed!!", e);
        }
    }

    public static String getProperty(String key) {
        return configs.get(key);
    }

    public static String getCaseProperty(String key){
        return  caseConfigs.get(key);
    }

    public static String setAccessToken(String clientId, String clientSecret, String user, String password, String env) throws UnsupportedEncodingException, JsonSyntaxException, MalformedURLException, URISyntaxException {
        String accessToken= SSOLoginUtils.ssoLogin(clientId,clientSecret,user,password,env);
        PropertyUtils.token=accessToken;
        return accessToken;
    }
    public static String getAccessToken() {
        return PropertyUtils.token;
    }


    public static String getProperty(String key, String defaultValue) {
        String value = configs.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 对config.properties配置文件中配置的dynamicCookie信息进行动态的修改(主要是动态替换ssoid信息)
     */
    public static void setDynamicCookie(){
        String baseCookie = configs.get("dynamicCookie");
        if(baseCookie==null||baseCookie.isEmpty()){
            LOGGER.error("You should add 'dynamicCookie' info in config.properties file!");
            return;
        }
        String ssoid = getAccessToken();
        String cookie = "";
        if(baseCookie.contains("ssoid=")){
            int startIndex = baseCookie.indexOf("ssoid");
            cookie = baseCookie.substring(0,startIndex).concat(" ssoid="+ssoid);
        }else{
            cookie=baseCookie.concat(" ssoid="+ssoid);
        }
        PropertyUtils.dynamicCookie = cookie;
    }

    public static String getDynamicCookie(){
        return PropertyUtils.dynamicCookie;
    }
}
