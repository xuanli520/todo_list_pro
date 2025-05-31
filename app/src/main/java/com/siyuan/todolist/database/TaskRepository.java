package com.siyuan.todolist.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.siyuan.todolist.models.Task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> activeTasks;
    private LiveData<List<Task>> completedTasks;

    // 添加获取带提醒的任务的方法
    public LiveData<List<Task>> getTasksWithReminders() {
        return taskDao.getTasksWithReminders();
    }

    // 添加同步方法获取带提醒的任务（用于非LifecycleOwner环境）
    public List<Task> getTasksWithRemindersSync() {
        try {
            return new GetTasksWithRemindersSyncTask(taskDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class GetTasksWithRemindersSyncTask extends AsyncTask<Void, Void, List<Task>> {
        private TaskDao taskDao;

        private GetTasksWithRemindersSyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return taskDao.getTasksWithRemindersSync();
        }
    }

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        activeTasks = taskDao.getActiveTasks();
        completedTasks = taskDao.getCompletedTasks();
    }
    
    // 插入任务
    public void insert(Task task) {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }
    
    // 更新任务
    public void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }
    
    // 删除任务
    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }
    
    // 删除所有任务
    public void deleteAllTasks() {
        new DeleteAllTasksAsyncTask(taskDao).execute();
    }
    
    // 获取所有任务
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
    
    // 获取活动任务（未完成）
    public LiveData<List<Task>> getActiveTasks() {
        return activeTasks;
    }
    
    // 获取已完成任务
    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }
    
    // 按到期日筛选任务
    public LiveData<List<Task>> getTasksByDueDate(Date startDate, Date endDate) {
        long startTimestamp = startDate != null ? startDate.getTime() : 0;
        long endTimestamp = endDate != null ? endDate.getTime() : Long.MAX_VALUE;
        return taskDao.getTasksByDueDate(startTimestamp, endTimestamp);
    }


    // 按优先级筛选任务
    public LiveData<List<Task>> getTasksByPriority(int priority) {
        return taskDao.getTasksByPriority(priority);
    }
    
    // 按类别筛选任务
    public LiveData<List<Task>> getTasksByCategory(String category) {
        return taskDao.getTasksByCategory(category);
    }
    
    // 搜索任务
    public LiveData<List<Task>> searchTasks(String query) {
        return taskDao.searchTasks(query);
    }
    
    // 异步任务类
    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        
        private InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }
    
    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        
        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }
    
    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        
        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }
    
    private static class DeleteAllTasksAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;
        
        private DeleteAllTasksAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        
        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.deleteAllTasks();
            return null;
        }
    }
}
