package com.merico.inftest.step;

import com.google.common.base.Splitter;
import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.commonutils.DateTimeUtils;
import com.merico.inftest.commonutils.JsonPathUtils;
import com.merico.inftest.commonutils.KeyValueStore;
import com.merico.inftest.context.Context;
import com.merico.inftest.response.Response;

import java.util.List;

/**
 * 设置变量 到 context 根据配置的key:jsonPath
 * 使用jsonPath从前一个返回中拿出具体的值value  然后把 key:value 存入content方便后面使用
 * 如果定义的jsonPath 在preResponse中不存在 则抛出异常
 *
 */

public class PlaceHolderStepCmd extends StepCmd {

    /**
     * String格式如下："key:jsonPath"
     */
    private List<String> placeHolder;

    public PlaceHolderStepCmd(List<String> placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Override
    public Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        placeHolder.forEach(s -> {
            //keyValueStore
            KeyValueStore keyValueStore = transformToKeyVal(s);
            Object paramValue = keyValueStore.getValue();

            if (null == paramValue) {
                return;
            }
            String paramPath = paramValue.toString();
            if (!paramPath.startsWith("$")) {
                //对于非json path 定义的 直接放入context todo 对于系统本身函数 多个需要支持时 这块需要优化
                String valueFinal = DateTimeUtils.replaceDateVar(paramPath);
                context.setContext(keyValueStore.getKey(), valueFinal);
                logger.info("set placeHolder key:{} , value:{}", keyValueStore.getKey(), valueFinal);
            } else {
                String body = preResponse.getBody();
                List<String> path = JsonPathUtils.getPath(body, paramPath);
                if (path == null || path.size() == 0) {
                    logger.warn("response：{} not exist jsonPath {}", body, paramPath);
                    //placeholder支持jsonpath 函数不存在不在抛出异常
                    //throw new RuntimeException(s + " 变量解析定义异常!请确认返回值: " + body + "存在" + paramValue.toString());
                }

                //对于json path定义的 从preResponse 获取结果
                Object value = JsonPathUtils.getValue(body, paramPath);
                if (null == value) {
                    logger.error(" PlaceHolderCmd {} not define！！ {} not fonund in response:{}", keyValueStore.getKey(), paramValue, preResponse);
                    context.setContext(keyValueStore.getKey(), "null");
                } else {
                    logger.info("set placeHolder key:{} , value:{}", keyValueStore.getKey(), value.toString());
                    context.setContext(keyValueStore.getKey(), value);
                }

            }

        });
        return preResponse;
    }

    /**
     * s中存在多个:
     *
     * @param s
     * @return
     */
    private KeyValueStore transformToKeyVal(String s) {
        int i = s.indexOf(":");
        return new KeyValueStore(s.substring(0, i), s.substring(i + 1, s.length()));

    }


}
