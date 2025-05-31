package com.siyuan.todolist.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.siyuan.todolist.R;
import com.siyuan.todolist.activities.MainActivity;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.ReminderManager;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final String CHANNEL_ID = "task_reminder_channel";
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到提醒广播");

        int taskId = intent.getIntExtra("task_id", -1);
        String taskTitle = intent.getStringExtra("task_title");
        String taskDescription = intent.getStringExtra("task_description");

        if (taskId == -1 || taskTitle == null) {
            Log.e(TAG, "任务ID或标题为空");
            return;
        }

        // 创建通知渠道（仅Android 8.0及以上需要）
        createNotificationChannel(context);

        // 创建点击通知后的Intent（打开MainActivity）
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                taskId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // 确保有这个图标资源
                .setContentTitle("待办任务提醒")
                .setContentText(taskTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (taskDescription != null && !taskDescription.isEmpty()) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(taskDescription);
            builder.setStyle(bigTextStyle);
        }

        // 显示通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(taskId, builder.build());
            Log.d(TAG, "通知已显示: " + taskTitle);

            // 设置下一次提醒
            // 这里应该从数据库获取完整的Task对象
            // 为简化示例，这部分在实际应用中需要完善
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "任务提醒";
            String description = "待办事项提醒通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
