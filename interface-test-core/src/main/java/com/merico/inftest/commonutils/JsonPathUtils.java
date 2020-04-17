package com.merico.inftest.commonutils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JsonPathUtils {
    private static Logger logger = LoggerFactory.getLogger(JsonPathUtils.class);

    private final static Configuration configuration = Configuration.defaultConfiguration().
            addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    private final static Configuration conf4Path = Configuration.builder()
            .options(Option.AS_PATH_LIST).build();

    public static Object getValue(String json, String path) {
        if (StringUtils.isBlank(json)) {
            return json;
        }

        DocumentContext parse = JsonPath.parse(json, configuration);
        Object read = parse.read(path);
        //fastjson反序列 浮点型 为bigDecimal jsonPath默认配置读取到数据为Double类型 导致断言失败 这里先临时处理下
        if (read != null && read instanceof Double) {
            String s = read.toString();
            read = new BigDecimal(s);
        }

        return read;
    }

    public static String setValue(String json , String path, Object value){
        DocumentContext parse = JsonPath.parse(json, configuration);
        return parse.set(path,value).jsonString();

    }

    public static void setValue2Document(DocumentContext documentContext, String jsonPath,Object newValue){
        documentContext.set(jsonPath, newValue).jsonString();
    }


    public static DocumentContext getDocumentContext(String json){
        return JsonPath.parse(json, configuration);


    }

    public static List<String> getPath(String json, String path) {
        List<String> paths = Lists.newArrayList();
        try {
            paths = JsonPath.using(conf4Path).parse(json).read(path);
        } catch (Exception exception) {
            logger.error("check Path error!! path:{},json:{} ", path, json);
            return paths;
        }
        return paths;
    }

    public static boolean isNotJson(String content) {

        if (StringUtils.isBlank(content)) {
            return true;
        }
        try {
            new JsonParser().parse(content);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

}
