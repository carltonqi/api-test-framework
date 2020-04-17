package com.merico.inftest.commonutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.merico.inftest.cases.LocalCallInfo;

public class LocalCallUtils {
    static Logger logger = LoggerFactory.getLogger(LocalCallUtils.class);


    public static Object[] buildParams(LocalCallInfo localCallInfo, Method declaredMethod) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        Map<String, Object> params = localCallInfo.getParams();
        return getParamObjectsArray(parameterTypes, params, declaredMethod.getName());
    }


    public static Object[] buildFactoryParams(LocalCallInfo localCallInfo, Method declaredMethod) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        Map<String, Object> params = localCallInfo.getConParams();
        return getParamObjectsArray(parameterTypes, params, declaredMethod.getName());

    }


    public static Object[] buildConstructorParams(LocalCallInfo localCallInfo, Constructor declaredConstructor) {
        return getParamObjectsArray(declaredConstructor.getParameterTypes(), localCallInfo.getConParams(), declaredConstructor.getName());
    }

    private static Object[] getParamObjectsArray(Class<?>[] parameterTypes, Map<String, Object> params, String methodName) {
        int paramNum = parameterTypes.length;
        List<Object> paramList = Lists.newArrayList();

        for (int i = 0; i < paramNum; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object methodParam = null;
            Object requestObject = params.get(String.valueOf(i));
            if (null == requestObject) {
                logger.error("请确认json文件中param已经正确填写！参考wiki:https://wiki.sankuai.com/pages/viewpage.action?pageId=1186792327");
                throw new RuntimeException(methodName + " 第" + i + "参数未在json文件中找到！");
            }
            //原生类型直接加入
            if (parameterType.isPrimitive() || parameterType.getSimpleName().equals("String")) {
                paramList.add(requestObject);
                continue;
            } else {
                methodParam = JSON.parseObject(requestObject.toString(), parameterType);
            }
            paramList.add(methodParam);
        }
        return paramList.toArray();
    }

}
