package com.siyuan.todolist.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.receivers.ReminderReceiver;

import java.util.Calendar;

public class ReminderManager {
    private static final String TAG = "ReminderManager";

    public static void scheduleReminder(Context context, Task task) {
        if (task.getReminderTime() == null || task.getDueDate() == null) {
            Log.d(TAG, "无法设置提醒: 缺少提醒时间或截止日期");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "无法获取AlarmManager服务");
            return;
        }

        // 从提醒时间获取小时和分钟
        Calendar reminderCal = Calendar.getInstance();
        reminderCal.setTime(task.getReminderTime());
        int hour = reminderCal.get(Calendar.HOUR_OF_DAY);
        int minute = reminderCal.get(Calendar.MINUTE);

        // 从截止日期获取年月日
        Calendar dueCal = Calendar.getInstance();
        dueCal.setTime(task.getDueDate());

        // 设置第一次提醒时间
        Calendar firstReminder = Calendar.getInstance();
        firstReminder.set(Calendar.YEAR, dueCal.get(Calendar.YEAR));
        firstReminder.set(Calendar.MONTH, dueCal.get(Calendar.MONTH));
        firstReminder.set(Calendar.DAY_OF_MONTH, dueCal.get(Calendar.DAY_OF_MONTH));
        firstReminder.set(Calendar.HOUR_OF_DAY, hour);
        firstReminder.set(Calendar.MINUTE, minute);
        firstReminder.set(Calendar.SECOND, 0);
        firstReminder.set(Calendar.MILLISECOND, 0);

        // 如果提醒时间已经过了，从明天开始
        if (firstReminder.getTimeInMillis() < System.currentTimeMillis()) {
            firstReminder.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 创建Intent和PendingIntent
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("task_id", task.getId());
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_description", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getId(), // 使用任务ID作为请求码，确保唯一性
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // 设置每日重复提醒，直到截止日期
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Android 12及以上，需要权限
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    firstReminder.getTimeInMillis(),
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0及以上
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    firstReminder.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            // 低版本Android
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    firstReminder.getTimeInMillis(),
                    pendingIntent
            );
        }

        Log.d(TAG, "提醒已设置: " + task.getTitle() + " 在 " +
                DateTimeUtils.formatDateTime(firstReminder.getTime()));
    }

    public static void cancelReminder(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "提醒已取消: 任务ID " + taskId);
        }
    }
}
