package com.merico.inftest.commonutils;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectUtils {

    static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    private static String methodWithParam = "(\\w+)\\((.*)\\)";

    private static Pattern pattern = Pattern.compile(methodWithParam);

    public static Method getMethod(Object classed, String methodName) {

        Method[] declaredMethods = classed.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(methodName)) {
                return declaredMethod;
            }
        }
        return null;
    }

    public static Method getMethod(Class classed, String methodName, int paramSize) {
        Method[] declaredMethods = classed.getMethods();
        String paramsType = Strings.EMPTY;
        Matcher matcher = pattern.matcher(methodName);
        if (matcher.matches() && matcher.groupCount() == 2) {
            //抛离出来方法名 和参数列表
            methodName = matcher.group(1);

            paramsType = matcher.group(2);
        }

        //methodName 如果相同的方法名 且参数个数一样 只是类型不一样 需要case 输入格式如下 sayHello(int ,com.tne.mdbi.cases.AssertInfo)
        for (Method declaredMethod : declaredMethods) {

            if (declaredMethod.getName().equals(methodName) && declaredMethod.getParameterTypes().length == paramSize) {
                if (StringUtils.equals(paramsType, Strings.EMPTY)) {
                    return declaredMethod;
                }

                List<String> paramTypeList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(paramsType);
                //传入了方法名 需要对比方法名
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if (IsClassNameEquals(paramTypeList, parameterTypes)) {
                    return declaredMethod;
                }
            }
        }
        return null;
    }

    private static boolean IsClassNameEquals(List<String> paramTypeList, Class<?>[] parameterTypes) {
        //不存在参数
        boolean noParam = (paramTypeList == null || paramTypeList.isEmpty())
                && (parameterTypes == null || parameterTypes.length == 0);

        if (noParam) {
            return true;
        }


        for (int i = 0; i < parameterTypes.length; i++) {
            if (!StringUtils.equals(parameterTypes[i].getName(), paramTypeList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static Constructor getConstructor(Class classed, int paramSize) {
        Constructor[] declaredConstructors = classed.getDeclaredConstructors();
        for (Constructor declaredConstructor : declaredConstructors) {
            if (declaredConstructor.getParameterTypes().length == paramSize) {
                return declaredConstructor;
            }
        }
        return null;

    }

    public static Object invokeMethod(Method method, Object object, Object[] params) {
        if (null == method) {
            logger.error("method is null！！please check method!!! class:{} ", object.getClass().getName());
            throw new RuntimeException("method is null！！please check method!!!");
        }

        Object result = null;
        try {
            result = method.invoke(object, params);
            logger.debug("invokeMethod {} success. params:{}, paramsLength: {}", method.getName(), params.toString(),
                    params.length);
            return result;
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            logger.warn("method {} throw exception: {} ", method.getName(), targetException.toString());
            return resultException(targetException);
        } catch (Exception e) {
            logger.error("invoke method error!! method:{} class:{}. ", method.getName(), object.getClass().getName(),
                    e);
            throw new RuntimeException(
                    "invoke method error!! method:" + "method.getName()" + ". " + e.getCause().getMessage());
        }
    }

    public static Object getClassFromName(String className) {
        try {
            Class<?> aClass = Class.forName(className);
            Object newInstance = aClass.newInstance();
            return newInstance;
        } catch (ClassNotFoundException e) {

            logger.error("自定义过滤器未找到请检查过滤器名称配置！！！", e);
            throw new RuntimeException("自定义参数过滤器:" + className + " 未找到对应类！请检查！！！");
        } catch (Exception e) {

            logger.error("创建自定义过滤器失败！！！", e);
            throw new RuntimeException("创建自定义过滤器:" + className + " 失败");
        }
    }

    private static Map<String, String> resultException(Throwable throwable) {
        Map<String, String> reusltException = Maps.newLinkedHashMap();
        reusltException.put("exceptionName", throwable.getClass().getSimpleName());
        reusltException.put("messge", throwable.getMessage());
        reusltException.put("exceptionStr", throwable.toString());
        return reusltException;
    }


    /**
     * 反射单测使用无特殊意义
     * @param a
     * @param b
     */
    public void justForTest(String a,int b){

    }
}
