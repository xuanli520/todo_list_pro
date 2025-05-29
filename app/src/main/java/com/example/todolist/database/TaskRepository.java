package com.example.todolist.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.todolist.models.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 任务数据仓库，负责数据操作
public class TaskRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;
    private final ExecutorService executorService;

    // 构造函数
    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application); // 待实现getDatabase方法
        mTaskDao = db.taskDao();
        // mAllTasks = mTaskDao.getAllTasks(); // getAllTasks返回LiveData，待修改TaskDao
        executorService = Executors.newSingleThreadExecutor();
    }

    // 获取所有任务（LiveData）
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    // 插入任务
    public void insert(Task task) {
        executorService.execute(() -> {
            mTaskDao.insertTask(task);
        });
    }

    // 更新任务
    public void update(Task task) {
        executorService.execute(() -> {
            mTaskDao.updateTask(task);
        });
    }

    // 删除任务
    public void delete(Task task) {
        executorService.execute(() -> {
            mTaskDao.deleteTask(task);
        });
    }

    // TODO: 添加获取单个任务的方法
}
