package com.example.todolist.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.todolist.models.Task;

// Room数据库抽象类
@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    // 获取TaskDao的抽象方法
    public abstract TaskDao taskDao();

    // 单例模式获取数据库实例
    // TODO: 实现单例模式获取数据库实例
}
