package com.merico.inftest.step;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.cases.LocalCallInfo;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.commonutils.LocalCallUtils;
import com.merico.inftest.commonutils.ReaderFileUtils;
import com.merico.inftest.commonutils.ReflectUtils;
import com.merico.inftest.commonutils.RpcUtils;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

public class LocalCallStepCmd extends StepCmd {

    private LocalCallInfo localCallInfo;

    public LocalCallStepCmd() {
    }

    public LocalCallStepCmd(LocalCallInfo localCallInfo) {
        this.localCallInfo = localCallInfo;
    }

    public LocalCallInfo getLocalCallInfo() {
        return localCallInfo;
    }

    public void setLocalCallInfo(LocalCallInfo localCallInfo) {
        this.localCallInfo = localCallInfo;
    }

    @Override
    public Response doExcute(CaseInfo CaseInfo, Response preResponse, Context context) throws Throwable {
        try {
            Object object;
            if (localCallInfo.getFactory() == null) {
                object = newInstance();
            } else {
                object = newInstanceByFactory();
            }

            Method method = ReflectUtils.getMethod(object.getClass(), localCallInfo.getMethod(),
                    localCallInfo.getParams().size());
            Object[] params = LocalCallUtils.buildParams(localCallInfo, method);
            Object resultObject = ReflectUtils.invokeMethod(method, object, params);
            logger.info("local call step method {} return: {}", method.getName(), resultObject);

            // String resultJsonStr = JSON.toJSONString(resultObject, SerializerFeature.WriteMapNullValue);
            Map<String, Object> resultBody = Maps.newHashMap();
            resultBody.put(localCallInfo.getMethod(), resultObject);

            String resultJsonStr = JSON.toJSONString(resultBody, SerializerFeature.WriteMapNullValue);

            System.out.println("******************************res: " + resultJsonStr);

            return new Response(resultJsonStr.toString());
        } catch (Exception e) {
            logger.error("create local call error!!", e);
            throw new RuntimeException("create local call error!!" + e.getCause().getMessage());
        }
    }

    public Object newInstanceByFactory() {
        try {
            Method method = ReflectUtils.getMethod(Class.forName(localCallInfo.getFactory()),
                    localCallInfo.getFactoryMethod(), localCallInfo.getConParams().size());
            Object[] params = LocalCallUtils.buildFactoryParams(localCallInfo, method);
            Object resultObject = ReflectUtils.invokeMethod(method, Class.forName(localCallInfo.getServiceInterface()),
                    params);
            return resultObject;
        } catch (Exception e) {
            logger.error("create local call error in newInstanceByFactory!!", e);
            throw new RuntimeException("create local call error in newInstanceByFactory!!" + e.getCause().getMessage());
        }
    }

    public Object newInstance() {
        try {
            Class clazz = Class.forName(localCallInfo.getServiceInterface());
            Constructor con = ReflectUtils.getConstructor(clazz, localCallInfo.getConParams().size());
            localCallInfo.getConParams();
            Object[] params = LocalCallUtils.buildConstructorParams(localCallInfo, con);
            return con.newInstance(params);
        } catch (Exception e) {
            logger.error("create local call error in newInstance!!", e);
            throw new RuntimeException("create local call error in newInstance!!" + e.getCause().getMessage());
        }
    }

}
