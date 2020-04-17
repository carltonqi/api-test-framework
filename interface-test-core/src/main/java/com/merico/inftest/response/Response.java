package com.merico.inftest.response;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.PathNotFoundException;
import com.merico.inftest.commonutils.InterfaceAssert;
import com.merico.inftest.commonutils.JsonPathUtils;
import com.merico.inftest.exception.AssertExceptionMsg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.testng.internal.EclipseInterface.*;

/**
 * 执行返回 结果存贮
 */

public class Response {

    private static final String REGEX = "^regex(.*)$";
    private String body;
    Logger logger = LoggerFactory.getLogger("com.tne.mdbi.step.AssertStepCmd");

    public Response() {
    }

    public Response(String body) {
        this.body = body;
    }

    public String getBody() {

        if (null == body) {
            return body;
        }

        if (JsonPathUtils.isNotJson(body)) {
            //返回值为非json格式的处理 强制转换成json格式
            Map<String, String> result = Maps.newHashMap();
            result.put("result", body);
            return JSON.toJSONString(result);
        }
        return body;
    }


    public void verify(Map<String, Object> expectBody) {
        List<String> bodyErrorList = Lists.newArrayList();
        assertBody((Map<String, Object>) expectBody.get("body"), bodyErrorList);

        List<String> keyExistError = Lists.newArrayList();
        assertkeyExist((List<String>) expectBody.get("keyExist"), keyExistError);

        List<String> excludeError = Lists.newArrayList();
        assertExclude((List<String>) expectBody.get("exclude"), excludeError);

        if (!bodyErrorList.isEmpty() || !keyExistError.isEmpty()
                || !excludeError.isEmpty()) {
            throw new AssertionError(AssertExceptionMsg.genertaMssage(bodyErrorList, keyExistError, excludeError));
        }
    }

    public Boolean verifyBool(Map<String, Object> expectBody, Boolean flag, Boolean breaked) {
        List<String> bodyErrorList = Lists.newArrayList();
        assertBody((Map<String, Object>) expectBody.get("body"), bodyErrorList);

        List<String> keyExistError = Lists.newArrayList();
        assertkeyExist((List<String>) expectBody.get("keyExist"), keyExistError);

        List<String> excludeError = Lists.newArrayList();
        assertExclude((List<String>) expectBody.get("exclude"), excludeError);

        List<String> failedError = Lists.newArrayList();
        assertFailed((Map<String, List<Object>>) expectBody.get("failed"), failedError);

        if (!bodyErrorList.isEmpty() || !keyExistError.isEmpty()
                || !excludeError.isEmpty()) {
            {
                if (!failedError.isEmpty()) {
                    if (breaked) {
                        throw new AssertionError(AssertExceptionMsg.genertaMssage(failedError, keyExistError, excludeError));
                    } else {
                        return true;
                    }
                }

                if (breaked && flag) {
                    throw new AssertionError(AssertExceptionMsg.genertaMssage(bodyErrorList, keyExistError, excludeError));
                }
                bodyErrorList.clear();
                keyExistError.clear();
                excludeError.clear();
                return false;
            }
        }
        return true;
    }

    private void assertExclude(List<String> exclude, List<String> errorList) {
        if (exclude == null || exclude.size() == 0) {
            return;
        }

        if (StringUtils.isBlank(body)) {
            return;
        }
        exclude.forEach(key -> {
            List<String> path = JsonPathUtils.getPath(body, key);
            if (path != null && path.size() != 0) {
                String errorMsg = formatKeyExist(key, body, true);
                logger.error(errorMsg);
                errorList.add(errorMsg);
            }
        });
    }

    private void assertkeyExist(List<String> keyExist, List<String> errorList) {
        if (keyExist == null || keyExist.size() == 0) {
            return;
        }
        keyExist.forEach(key -> {
            List<String> path = JsonPathUtils.getPath(getBody(), key);
            if (path == null || path.size() == 0) {
                String errorMsg = formatKeyExist(key, body, false);
                logger.error(errorMsg);
                errorList.add(errorMsg);
            }
        });


    }

    private void assertBody(Map<String, Object> expectBody, List<String> errorList) {
        if (null == expectBody) {
            return;
        }
        //todo 如何断言结果返回为空 这块可以在思考下
        if (expectBody.isEmpty() && getBody() != null) {
            String msg = "期望返回:null ,实际返回为:" + getBody();
            logger.error(msg);
            errorList.add(msg);
            return;
        }
        // 断言非空，返回值为null 或者 ""
        if (!expectBody.isEmpty() && StringUtils.isBlank(body)) {
            logger.error("返回值response为 null or empty! body:{} .", body);
            errorList.add("返回值response为 null or empty! body: " + body);
            return;
        }


        Set<Map.Entry<String, Object>> entries = expectBody.entrySet();
        entries.forEach(entry -> {
            String key = entry.getKey();
            Object expectValue = entry.getValue();
            Object realValue = null;
            try {
                realValue = JsonPathUtils.getValue(getBody(), key);
            } catch (PathNotFoundException e) {
                logger.error("key:{} not exit in body:{}", key, getBody());
                errorList.add("key:" + key + " not exit in body!");
                return;
            }

            if (StringUtils.containsIgnoreCase(key, "id")
                    && realValue != null
                    && expectValue != null
                    && realValue.getClass().getSimpleName().equals("Integer")) { //由于自定义变量的存在 id的Integer 类型转换为 字符串比对
                bodyAssert(expectValue.toString(), realValue.toString(), key, errorList);
            } else {
                bodyAssert(expectValue, realValue, key, errorList);
            }
        });

    }

    private void assertFailed(Map<String, List<Object>> expectFailed, List<String> errorList) {
        if (null == expectFailed) {
            return;
        }
        // 断言非空，返回值为null 或者 ""
        if (!expectFailed.isEmpty() && StringUtils.isBlank(body)) {
            logger.error("返回值response为 null or empty! body:{} .", body);
            errorList.add("返回值response为 null or empty! body: " + body);
            return;
        }

        Set<Map.Entry<String, List<Object>>> entries = expectFailed.entrySet();
        entries.forEach(entry -> {
            String key = entry.getKey();
            List<Object> expectValues = entry.getValue();
            Object realValue = null;
            try {
                realValue = JsonPathUtils.getValue(getBody(), key);
            } catch (PathNotFoundException e) {
                errorList.add("key:" + key + " not exit in body!");
                return;
            }
            if (realValue != null && expectValues != null) {
                for (Object expectValue : expectValues) {
                    boolean assertEquals = InterfaceAssert.assertEquals(realValue, expectValue);
                    if (assertEquals) {
                        String msg = key + "=" + expectValue + " ,中断循环检查;";
                        errorList.add(msg);
                        logger.error(msg);
                        return;
                    }
                }
            }
        });
    }


    /**
     * body 断言 目前支持正则表达式 形式为 regex(.*) 如果需要支持其他函数  本方法需要重构
     *
     * @param expectValue
     * @param realValue
     * @param key
     * @param errorList
     */
    private void bodyAssert(Object expectValue, Object realValue, String key, List<String> errorList) {

        // regex 函数支持
        if ((expectValue != null) && (expectValue instanceof String) && ((String) expectValue).matches(REGEX)) {
            //函数 取对应的值进行 正则匹配
            String regex = getRegex(expectValue.toString());
            Pattern compile = Pattern.compile(regex);
            boolean result = compile.matcher(String.valueOf(realValue)).matches();
            if (!result) {
                String errorMsg = format(realValue, expectValue, key);
                logger.error(errorMsg);
                errorList.add(errorMsg);
            }
            return;
        }

        //contains 函数支持
        if (expectValue != null && expectValue.toString().trim().startsWith("contains")) {
            String tmp = expectValue.toString().trim();
            String errorMsg = format(realValue, expectValue, key);

            if (realValue == null) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            expectValue = tmp.substring(9, tmp.length() - 1);
            if (!realValue.toString().contains(expectValue.toString())) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            return;
        }

        //notContains 函数支持
        if (expectValue != null && expectValue.toString().trim().startsWith("notContains")) {
            String tmp = expectValue.toString().trim();
            //String errorMsg = key + "不期望包含: " + expectValue + ",实际为:" + realValue;
            String errorMsg = format(realValue, expectValue, key);
            if (realValue == null) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            expectValue = tmp.substring(12, tmp.length() - 1);
            if (realValue.toString().contains(expectValue.toString())) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            return;
        }

//        not null 函数支持
        if (expectValue != null && expectValue.toString().trim().startsWith("notBlank")) {
            String errorMsg = format(realValue, expectValue, key);
            if (realValue == null || realValue.toString().trim().length() == 0) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            return;
        }

        if (expectValue != null && expectValue.toString().trim().startsWith("greaterThan")) {
            String tmp = expectValue.toString().trim();
            //String errorMsg = key + "期望大于: " + expectValue + ",实际为: " + realValue;
            String errorMsg = format(realValue, expectValue, key);

            if (realValue == null) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            expectValue = tmp.substring(12, tmp.length() - 1);
            int expectIntValue = Integer.valueOf(String.valueOf(expectValue)).intValue();
            int realIntValue = Integer.valueOf(String.valueOf(realValue)).intValue();
            if (realIntValue <= expectIntValue) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            return;
        }

        if (expectValue != null && expectValue.toString().trim().startsWith("lessThan")) {
            String tmp = expectValue.toString().trim();
            //String errorMsg = key + "期望小于: " + expectValue + ",实际为: " + realValue;
            String errorMsg = format(realValue, expectValue, key);

            if (realValue == null) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            expectValue = tmp.substring(9, tmp.length() - 1);
            int expectIntValue = Integer.valueOf(String.valueOf(expectValue)).intValue();
            int realIntValue = Integer.valueOf(String.valueOf(realValue)).intValue();
            if (realIntValue >= expectIntValue) {
                logger.error(errorMsg);
                errorList.add(errorMsg);
                return;
            }
            return;
        }

        boolean assertEquals = InterfaceAssert.assertEquals(realValue, expectValue);
        if (!assertEquals) {
            String msg = format(realValue, expectValue, key);
            errorList.add(msg);
            logger.error(msg);
            return;
        }
    }

    private String format(Object realValue, Object expectValue, String key) {
        String formatted = "";
        if (null != key) {
            formatted = key + " ";
        }
        String realValueStr = "";
        if (null == realValue) {
            realValueStr = "null";
        } else if (realValue instanceof String) {
            realValueStr = realValue.toString();
        } else {
            realValueStr = JSON.toJSONString(realValue);
        }

        String expectValueStr = "";
        if (expectValue instanceof String) {
            expectValueStr = expectValue.toString();
        } else {
            expectValueStr = JSON.toJSONString(expectValue);
        }
        return formatted + ASSERT_LEFT + expectValueStr + ASSERT_MIDDLE + realValueStr + ASSERT_RIGHT;
    }

    private String formatKeyExist(String key, String respones, boolean exist) {
        if (exist) {
            return key + " exist in response: " + respones;
        } else {
            return key + " not exist in response: " + respones;
        }
    }

    private String getRegex(String expectValue) {
        String regex = expectValue.substring("regex(".length(), expectValue.lastIndexOf(")"));
        return regex;

    }


    @Override
    public String toString() {
        return "Response:{" +
                "body='" + body + '\'' +
                '}';
    }
}
