package com.merico.inftest.commonutils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.merico.inftest.cases.RequestInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RpcUtils {

    static Logger logger = LoggerFactory.getLogger(RpcUtils.class);

    private static List<String> listTypeNames = ImmutableList.of("List", "Set");

    /**
     * url thrift:com.xxx.xxx.xxx.xxservices or pigeon:com.xxx.xxx.xx.xxservices
     *
     * @param url
     * @return
     */
    public static String getRpcService(String url) {
        int i = url.indexOf(":");
        return url.substring(i + 1, url.length());
    }

    public static Object[] buildParams(RequestInfo requestInfo, Method declaredMethod, String rpcType) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        Map<String, Object> params = requestInfo.getParams();
        int paramNum = parameterTypes.length;
        List<Object> paramList = Lists.newArrayList();

        for (int i = 0; i < paramNum; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object methodParam = null;
            Object requestObject = params.get(String.valueOf(i));
            if (null == requestObject) {
                logger.error("请确认json文件中param已经正确填写！参考wiki:https://wiki.sankuai.com/pages/viewpage.action?pageId=1186792327");
                throw new RuntimeException(declaredMethod.getName() + " 第" + i + "参数未在json文件中找到！");
            }
            //原生类型直接加入
            if (parameterType.isPrimitive()) {
                paramList.add(requestObject);
                continue;
            }

            if (parameterType.getSimpleName().equals("String")) {
                paramList.add(String.valueOf(requestObject));
                continue;
            }

            if (listTypeNames.contains(parameterType.getSimpleName())) {
                //由于proxy 代理中不存在list的泛型 只能去 原始services中去查找泛型
                Object listGenerics = getListGenerics(rpcType, requestInfo, parameterTypes, declaredMethod.getName(), i);
                List<?> methodParamTmp = JSON.parseArray(requestObject.toString(), (Class<? extends Object>) listGenerics);

                //对于Set单独处理
                if (parameterType.getSimpleName().equals("Set")) {
                    HashSet hashSet = Sets.newLinkedHashSet();
                    for (Object tmp : methodParamTmp) {
                        hashSet.add(tmp);
                    }
                    methodParam = hashSet;
                } else {
                    methodParam = methodParamTmp;
                }


            } else {
                methodParam = JSON.parseObject(requestObject.toString(), parameterType);
            }
            paramList.add(methodParam);
        }

        return paramList.toArray();
    }


    private static Object getListGenerics(String rpcType, RequestInfo requestInfo, Class<?>[] parameterTypes, String methodName, int index) {
        if ("thrift".equals(rpcType)) {
            return getListGenericsForThrift(requestInfo, parameterTypes, methodName, index);
        }
        if ("pigeon".equals(rpcType)) {
            return getListGenericsForPigeon(requestInfo, parameterTypes, methodName, index);
        }
        return null;
    }

    private static Object getListGenericsForThrift(RequestInfo requestInfo, Class<?>[] parameterTypes, String methodName, int index) {
        Class<?> servicesClass = null;
        try {
            servicesClass = Class.forName(getRpcService(requestInfo.getUrl()));
            Class<?>[] declaredClasses = servicesClass.getDeclaredClasses();
            Type[] genericParameterTypes = null;
            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.getSimpleName().equals("Iface")) {
                    Method declaredMethod = declaredClass.getDeclaredMethod(methodName, parameterTypes);
                    genericParameterTypes = declaredMethod.getGenericParameterTypes();
                    break;
                }
            }
            ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) genericParameterTypes[index];
            return genericParameterType.getActualTypeArguments()[0];
        } catch (Exception e) {
            logger.error("build List param error!!!!", e);
            throw new RuntimeException("build List param error" + e.getMessage());
        }
    }


    private static Object getListGenericsForPigeon(RequestInfo requestInfo, Class<?>[] parameterTypes, String methodName, int index) {
        Class<?> servicesClass = null;
        try {
            servicesClass = Class.forName(getRpcService(requestInfo.getServiceInterface()));
            Method declaredMethod = servicesClass.getDeclaredMethod(methodName, parameterTypes);
            Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
            ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) genericParameterTypes[index];
            return genericParameterType.getActualTypeArguments()[0];
        } catch (Exception e) {
            logger.error("build List param error!!!!", e);
            throw new RuntimeException("build List param error" + e.getMessage());
        }
    }

}
