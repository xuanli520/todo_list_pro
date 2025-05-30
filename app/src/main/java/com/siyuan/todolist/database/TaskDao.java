package com.siyuan.todolist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.siyuan.todolist.models.Task;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);
    
    @Update
    void update(Task task);
    
    @Delete
    void delete(Task task);
    
    @Query("DELETE FROM task_table")
    void deleteAllTasks();
    
    @Query("SELECT * FROM task_table ORDER BY completed ASC, dueDate ASC")
    LiveData<List<Task>> getAllTasks();
    
    @Query("SELECT * FROM task_table WHERE completed = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getActiveTasks();
    
    @Query("SELECT * FROM task_table WHERE completed = 1 ORDER BY dueDate DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM task_table WHERE dueDate BETWEEN :startDateTimestamp AND :endDateTimestamp ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByDueDate(long startDateTimestamp, long endDateTimestamp);


    @Query("SELECT * FROM task_table WHERE priority = :priority ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByPriority(int priority);
    
    @Query("SELECT * FROM task_table WHERE category = :category ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByCategory(String category);
    
    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    LiveData<List<Task>> searchTasks(String searchQuery);
}
