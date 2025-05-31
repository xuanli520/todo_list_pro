package com.siyuan.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat WEEKDAY_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());

    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date time) {
        if (time == null) return "";
        return TIME_FORMAT.format(time);
    }

    public static String formatDateTime(Date dateTime) {
        if (dateTime == null) return "";
        return DATE_TIME_FORMAT.format(dateTime);
    }

    /**
     * 返回用户友好的日期字符串，如"今天"、"明天"、"星期五"等
     */
    public static String getFriendlyDateString(Date date) {
        if (date == null) return "";

        Calendar givenCal = Calendar.getInstance();
        givenCal.setTime(date);

        Calendar todayCal = Calendar.getInstance();

        // 重置时间部分，只比较日期
        resetTimeFields(givenCal);
        resetTimeFields(todayCal);

        // 计算给定日期与今天的差距（天数）
        long diffInMillis = givenCal.getTimeInMillis() - todayCal.getTimeInMillis();
        int diffInDays = (int) (diffInMillis / (24 * 60 * 60 * 1000));

        switch (diffInDays) {
            case -1:
                return "昨天";
            case 0:
                return "今天";
            case 1:
                return "明天";
        }

        // 如果是本周内的日期，显示星期几
        if (diffInDays > 1 && diffInDays < 7) {
            return WEEKDAY_FORMAT.format(date);
        }

        // 其他情况显示完整日期
        return formatDate(date);
    }

    /**
     * 检查给定日期是否已过期
     */
    public static boolean isOverdue(Date date) {
        if (date == null) return false;

        // 只比较日期部分
        Calendar givenCal = Calendar.getInstance();
        givenCal.setTime(date);

        Calendar todayCal = Calendar.getInstance();

        // 重置时间为当天结束
        givenCal.set(Calendar.HOUR_OF_DAY, 23);
        givenCal.set(Calendar.MINUTE, 59);
        givenCal.set(Calendar.SECOND, 59);

        // 如果给定日期早于当前时间，则已过期
        return givenCal.getTime().before(todayCal.getTime());
    }

    /**
     * 重置日历对象的时间字段为零，只保留日期部分
     */
    private static void resetTimeFields(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 获取一天的开始时间
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束时间
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 在给定日期上增加指定天数
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}
