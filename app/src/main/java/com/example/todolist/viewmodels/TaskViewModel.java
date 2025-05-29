package com.example.todolist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todolist.database.TaskRepository;
import com.example.todolist.models.Task;

import java.util.List;

// 任务ViewModel，负责准备和管理UI相关的数据
public class TaskViewModel extends AndroidViewModel {

    private TaskRepository mRepository;
    private final LiveData<List<Task>> mAllTasks;

    // 构造函数
    public TaskViewModel (Application application) {
        super(application);
        mRepository = new TaskRepository(application);
        mAllTasks = mRepository.getAllTasks();
    }

    // 获取所有任务（LiveData）
    public LiveData<List<Task>> getAllTasks() { return mAllTasks; }

    // 插入任务
    public void insert(Task task) { mRepository.insert(task); }

    // 更新任务
    public void update(Task task) { mRepository.update(task); }

    // 删除任务
    public void delete(Task task) { mRepository.delete(task); }

    // TODO: 添加获取单个任务、按条件过滤任务等方法
}
