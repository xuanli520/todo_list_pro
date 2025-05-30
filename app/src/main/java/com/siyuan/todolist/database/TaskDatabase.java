package com.siyuan.todolist.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.siyuan.todolist.models.Task;

import java.util.Date;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    
    private static TaskDatabase instance;
    
    public abstract TaskDao taskDao();
    
    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TaskDatabase.class, "task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    
    // 在首次创建数据库时可以预填充一些示例数据
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;
        
        private PopulateDbAsyncTask(TaskDatabase db) {
            taskDao = db.taskDao();
        }
        
        @Override
        protected Void doInBackground(Void... voids) {
            // 添加一些示例任务
            Date today = new Date();
            
            taskDao.insert(new Task("欢迎使用TodoList", 
                    "这是一个示例任务，您可以点击编辑或删除", 
                    today, null, 2, "个人"));
            
            // 明天的日期
            Date tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
            
            taskDao.insert(new Task("学习Android开发", 
                    "学习Room、LiveData和ViewModel的使用", 
                    tomorrow, null, 3, "学习"));
            
            return null;
        }
    }
}
