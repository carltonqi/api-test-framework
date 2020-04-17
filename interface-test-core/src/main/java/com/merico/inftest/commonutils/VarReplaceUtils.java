package com.merico.inftest.commonutils;

import com.google.common.collect.Lists;
import com.merico.inftest.cases.*;
import com.merico.inftest.context.Context;
import com.merico.inftest.step.*;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 处理上下文中定义的变量  区分不同的 stepCmd 执行不同的替换策略
 */

public class VarReplaceUtils {

    public static void varReplace(StepCmd stepCmd, Context context) {
        if (stepCmd == null) {
            return;
        }

//        if(stepCmd instanceof MafkaStepCmd){
//            replaceMafka((MafkaStepCmd) stepCmd, context);
//        }

        if (stepCmd instanceof RequestStepCmd) {
            replaceRequest((RequestStepCmd) stepCmd, context);
            return;
        }

        if (stepCmd instanceof AssertStepCmd) {
            replaceAssert((AssertStepCmd) stepCmd, context);
            return;
        }

        if (stepCmd instanceof SqlExecStepCmd) {
            replaceSqlCmd((SqlExecStepCmd) stepCmd, context);
            return;
        }

        if (stepCmd instanceof LocalCallStepCmd) {
            replaceLocalCall((LocalCallStepCmd) stepCmd, context);
            return;
        }

        if (stepCmd instanceof LoopCheckStepCmd) {
            replaceLoopCheck((LoopCheckStepCmd) stepCmd, context);
            return;
        }
    }

    private static void replaceSqlCmd(SqlExecStepCmd sqlExecStepCmd, Context context) {
        String sql = sqlExecStepCmd.getSql();
        String finalSql = replaceString(sql, context);
        sqlExecStepCmd.setSql(finalSql);
    }

    //datasets使用 分离出 public方法
    public static void replaceDbAssertInfo(DBAssertInfo dbAssertInfo, Context context) {
        if (dbAssertInfo == null) {
            return;
        }
        String sql = dbAssertInfo.getSql();
        dbAssertInfo.setSql(replaceString(sql, context));
        Map<String, Object> body = dbAssertInfo.getBody();
        replaceMap(body, context);
    }

    private static void replaceAssert(AssertStepCmd assertStepCmd, Context context) {
        AssertInfo assertInfo = assertStepCmd.getAssertInfo();
        replaceAssertInfo(assertInfo, context);
    }

    //datasets使用 分离出 public方法
    public static void replaceAssertInfo(AssertInfo assertInfo, Context context) {
        if (assertInfo == null) {
            return;
        }
        replaceMap(assertInfo.getBody(), context);
        List<String> keyExsits = replaceList(assertInfo.getKeyExist(), context);
        assertInfo.setKeyExist(keyExsits);
        assertInfo.setExclude(replaceList(assertInfo.getExclude(), context));
    }

    private static void replaceLoopCheck(LoopCheckStepCmd loopCheckStepCmd, Context context) {
        LoopCheckInfo loopCheckInfo = loopCheckStepCmd.getLoopCheckInfo();
        replaceRequestInfo(loopCheckInfo.getRequestInfo(), context);
        replaceAssertInfo(loopCheckInfo.getAssertInfo(), context);
        replaceLocalCallInfo(loopCheckInfo.getLocalCallInfo(),context);
    }

//    private static void replaceMafka(MafkaStepCmd mafkaStepCmd, Context context) {
//        MafkaInfo mafkaInfo = mafkaStepCmd.getMafkaInfo();
//        replaceMafkaInfo(mafkaInfo, context);
//    }
//
//    public static void replaceMafkaInfo(MafkaInfo mafkaInfo, Context context) {
//        if (mafkaInfo == null) {
//            return;
//        }
//        // 处理变量
//        String mafkaBGNamespace = replaceString(mafkaInfo.getMafkaBGNamespace(), context);
//        mafkaInfo.setMafkaBGNamespace(mafkaBGNamespace);
//
//        String mafkaClientAppkey = replaceString(mafkaInfo.getMafkaClientAppkey(), context);
//        mafkaInfo.setMafkaClientAppkey(mafkaClientAppkey);
//
//        String mafkaTopic = replaceString(mafkaInfo.getMafkaTopic(), context);
//        mafkaInfo.setMafkaTopic(mafkaTopic);
//
//        String mafkaMsgBody = replaceString(mafkaInfo.getMafkaMsgBody(), context);
//        mafkaInfo.setMafkaMsgBody(mafkaMsgBody);
//    }

    private static void replaceRequest(RequestStepCmd requestStepCmd, Context context) {
        RequestInfo requestInfo = requestStepCmd.getRequestInfo();
        replaceRequestInfo(requestInfo, context);


    }

    //datasets使用 分离出 public方法
    public static void replaceRequestInfo(RequestInfo requestInfo, Context context) {
        if (requestInfo == null) {
            return;
        }
        //处理url  string
        String url = requestInfo.getUrl();
        requestInfo.setUrl(replaceString(url, context));
        //处理param map
        Map<String, Object> params = requestInfo.getParams();
        replaceMap(params, context);
        // 处理header map
        Map<String, Object> headers = requestInfo.getHeaders();
        replaceMap(headers, context);

        // 处理json  string
        String json = requestInfo.getEntity();
        String jsonReplace = replaceString(json, context);
        requestInfo.setEntity(jsonReplace);
    }

    private static void replaceLocalCall(LocalCallStepCmd localCallStepCmd, Context context) {
        LocalCallInfo localCallInfo = localCallStepCmd.getLocalCallInfo();
        replaceLocalCallInfo(localCallInfo, context);
    }

    public static void replaceLocalCallInfo(LocalCallInfo localCallInfo, Context context) {
        if (localCallInfo == null) {
            return;
        }
        String factory = localCallInfo.getFactory();
        localCallInfo.setFactory(replaceString(factory, context));
        String factoryMethod = localCallInfo.getFactoryMethod();
        localCallInfo.setFactoryMethod(replaceString(factoryMethod, context));
        String serviceInterface = localCallInfo.getServiceInterface();
        localCallInfo.setServiceInterface(replaceString(serviceInterface, context));
        String method = localCallInfo.getMethod();
        localCallInfo.setMethod(replaceString(method, context));
        //处理param map
        Map<String, Object> params = localCallInfo.getParams();
        replaceMap(params, context);
        //处理conParams
        Map<String, Object> conParams = localCallInfo.getConParams();
        replaceMap(conParams, context);
    }

    //需要处理placeHolder 所以此方法标注为public
    public static List<String> replaceList(List<String> keyExist, Context context) {

        if (keyExist == null || keyExist.size() == 0) {
            return Collections.emptyList();
        }

        List<String> strAfterRepalce = Lists.newArrayList();
        keyExist.forEach(name -> {
            Object strReplace = context.replace(name);
            if (strReplace != null) {
                strAfterRepalce.add(strReplace.toString());
            }
        });

        return strAfterRepalce;
    }

    private static void replaceMap(Map<String, Object> params, Context context) {
        if (params == null || params.size() == 0) {
            return;
        }

        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String k = next.getKey();
            Object v = next.getValue();
            if (null != v && null != k) {

                //处理key
                String replaceKeyStr = k;
                Object replaceKey = context.replace(k);
                if (null != replaceKey && !StringUtils.equals(replaceKey.toString(), k)) {
                    //需要对key进行替换
                    //移除老的key 取出老的value
                    Object value = params.get(replaceKeyStr);
                    iterator.remove();

                    //放入新的key 及老的value
                    replaceKeyStr = replaceKey.toString();
                    params.put(replaceKeyStr,value);
                }
                Object replaceValue = null;
                replaceValue = context.replace(v.toString());
                //只对变量进行替换 非变量不进行替换
                if (replaceValue != null && !replaceValue.toString().equals(v.toString())) {
                    params.put(replaceKeyStr, replaceValue);
                }
            }
        }
        return;
    }

    private static String replaceString(String originStr, Context context) {
        if (StringUtils.isBlank(originStr)) {
            return null;
        }
        Object replace = context.replace(originStr);
        if (replace != null) {
            return replace.toString();
        }

        return originStr;
    }
}
