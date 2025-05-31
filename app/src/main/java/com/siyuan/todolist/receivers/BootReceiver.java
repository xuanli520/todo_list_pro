package com.siyuan.todolist.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.siyuan.todolist.database.TaskRepository;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.ReminderManager;

import java.util.Date;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "设备重启，恢复提醒");

            // 获取Application上下文
            Context applicationContext = context.getApplicationContext();

            // 使用TaskRepository同步方法获取带提醒的任务
            TaskRepository repository = new TaskRepository(
                    (android.app.Application) applicationContext);

            // 使用同步方法而非LiveData观察者
            List<Task> tasksWithReminders = repository.getTasksWithRemindersSync();

            if (tasksWithReminders != null && !tasksWithReminders.isEmpty()) {
                Date now = new Date();
                for (Task task : tasksWithReminders) {
                    // 只恢复未完成且截止日期未到的任务
                    if (!task.isCompleted() && task.getDueDate().after(now)) {
                        Log.d(TAG, "恢复任务提醒: " + task.getTitle());
                        ReminderManager.scheduleReminder(context, task);
                    }
                }
            }
        }
    }
}
