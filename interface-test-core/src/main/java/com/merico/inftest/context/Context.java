package com.merico.inftest.context;

import com.google.common.collect.Maps;
import com.merico.inftest.commonutils.DateTimeUtils;
import com.merico.inftest.commonutils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String VAR_PATTERN = "\\$\\{([a-zA-Z0-9 \\.\\$_\\-]*)\\}";

    private final String ONLY_VAR_PATTERN_STR = "^\\$\\{([a-zA-Z0-9 \\.\\$_\\-\\s]*)\\}$";

    private final Pattern pattern = Pattern.compile(VAR_PATTERN);

    private final Pattern VAR_ONLY_PARRERN = Pattern.compile(ONLY_VAR_PATTERN_STR);

    Map<String, Object> context = Maps.newHashMap();

    public Context() {
    }

    public Context(Map<String, Object> context) {
        this.context = context;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }


    public void setContext(String key, Object value) {
        context.put(key, value);
    }

    public Object getContextValue(String key) {
        Object value = context.get(key);
        return value;
    }

    /**
     * 若需要设置的变量未找到 则这是为"null"
     *
     * @param var
     * @return
     */
    public Object replace(String var) {
        if (StringUtils.isBlank(var)) {
            return null;
        }

        //先处理时间函数问题
        if (DateTimeUtils.hasDateVar(var)) {
            var = DateTimeUtils.replaceDateVar(var);
        }

        //传入的为一个变量 ${user.id}
        if (VAR_ONLY_PARRERN.matcher(var).matches()) {
            String key = getKeyName(var);
            Object contextValue = getContextValue(key);
            //对于测试用例中频繁用到的变量也可以配置在config文件中
            if (null == contextValue) {
                contextValue = PropertyUtils.getCaseProperty(key);
            }
            if (null == contextValue) {
                logger.warn("{} not find var to replace!", var);
                return null;
            }
            return contextValue;
        }

        //传入的string中包含一个变量
        return partReplace(var);

    }

    private Object partReplace(String var) {
        Matcher matcher = pattern.matcher(var);
        while (matcher.find()) {
            String varName = matcher.group(0);
            String key = matcher.group(1);
            Object contextValue = getContextValue(key);

            if (null == contextValue) {
                contextValue = PropertyUtils.getCaseProperty(key);
            }

            if (contextValue == null) {
                continue;
            }
            var = StringUtils.replace(var, varName, contextValue.toString());

        }
        return var;
    }

    private String getKeyName(String var) {
        String key = var.substring(2, var.length() - 1);
        return key;
    }
}
