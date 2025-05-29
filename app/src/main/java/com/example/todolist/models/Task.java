package com.example.todolist.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 任务实体类
@Entity(tableName = "tasks") // 假设任务表名为 tasks
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private long dueDate; // 截止日期，使用时间戳
    private boolean isCompleted;

    // 构造函数
    public Task(String title, String description, long dueDate, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
