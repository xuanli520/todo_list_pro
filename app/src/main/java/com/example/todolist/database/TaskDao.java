package com.example.todolist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.models.Task;

import java.util.List;

// 任务数据访问对象接口
@Dao
public interface TaskDao {

    // 插入任务
    @Insert
    void insertTask(Task task);

    // 更新任务
    @Update
    void updateTask(Task task);

    // 删除任务
    @Delete
    void deleteTask(Task task);

    // 获取所有任务
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC") // 假设任务表名为 tasks，按截止日期排序
    LiveData<List<Task>> getAllTasks();

    // 根据ID获取任务
    @Query("SELECT * FROM tasks WHERE id = :taskId") // 假设任务表名为 tasks
    Task getTaskById(int taskId);
}
