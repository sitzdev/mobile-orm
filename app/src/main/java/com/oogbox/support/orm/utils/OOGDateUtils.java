package com.oogbox.support.orm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OOGDateUtils {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";


    public static String getUTCDate() {
        return getUTCDate(new Date(), DEFAULT_FORMAT);
    }

    public static String getUTCDate(Date date, String defaultFormat) {
        return createDate(date, defaultFormat, true);
    }

    private static String createDate(Date date, String defaultFormat, Boolean utc) {
        SimpleDateFormat gmtFormat = new SimpleDateFormat();
        gmtFormat.applyPattern(defaultFormat);
        TimeZone gmtTime = (utc) ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault();
        gmtFormat.setTimeZone(gmtTime);
        return gmtFormat.format(date);
    }
}
