package com.example.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// 日期时间工具类
public class DateTimeUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    // 将时间戳格式化为字符串
    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // TODO: 添加更多日期时间相关工具方法
}
