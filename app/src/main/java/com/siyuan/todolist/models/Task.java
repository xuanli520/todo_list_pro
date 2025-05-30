package com.siyuan.todolist.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.siyuan.todolist.database.DateConverter;

import java.io.Serializable;
import java.util.Date;
@Entity(tableName = "task_table")
@TypeConverters(DateConverter.class)
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private Date dueDate;
    private Date reminderTime;
    private int priority; // 1=低, 2=中, 3=高
    private String category;
    private boolean completed;
    private Date createdDate;
    
    public Task(String title, String description, Date dueDate, 
                Date reminderTime, int priority, String category) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.reminderTime = reminderTime;
        this.priority = priority;
        this.category = category;
        this.completed = false;
        this.createdDate = new Date();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public Date getReminderTime() { return reminderTime; }
    public void setReminderTime(Date reminderTime) { this.reminderTime = reminderTime; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
