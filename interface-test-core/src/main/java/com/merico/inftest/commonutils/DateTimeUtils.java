package com.merico.inftest.commonutils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 目前仅支持 simpletimeFormat 暂时无法获取unixTimpStamp
 */

public class DateTimeUtils {


    private final static String DATA_PATTERN = "DATE\\((-?\\d+),((.[YyMmDdHhMmSs]{1,4}.){1,})\\)";

    private final static Pattern PATTERN = Pattern.compile(DATA_PATTERN);


    public static Boolean hasDateVar(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return PATTERN.matcher(text).find();

    }


    public static String replaceDateVar(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.groupCount() > 2) {
                String originStr = matcher.group(0);
                Integer offset = Integer.parseInt(matcher.group(1));
                String dataFormat = matcher.group(2);
                String dataStr = getDateStr(offset, dataFormat);
                text = StringUtils.replace(text, originStr, dataStr);
            }
        }
        return text;

    }

    private static String getDateStr(Integer offset, String dataFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DAY_OF_YEAR, offset);
        String format = simpleDateFormat.format(instance.getTime());
        return format;
    }

}
