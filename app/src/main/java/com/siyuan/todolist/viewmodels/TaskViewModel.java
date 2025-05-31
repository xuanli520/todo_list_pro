package com.siyuan.todolist.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.siyuan.todolist.database.TaskRepository;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.siyuan.todolist.utils.ReminderManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository repository;

    // LiveData
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> activeTasks;
    private LiveData<List<Task>> completedTasks;
    private LiveData<List<Task>> tasksWithReminders;

    // 过滤和排序参数
    private MutableLiveData<String> filterCategory = new MutableLiveData<>();
    private MutableLiveData<Integer> filterPriority = new MutableLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();

    // 当前选中的任务
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);

        allTasks = repository.getAllTasks();
        activeTasks = repository.getActiveTasks();
        completedTasks = repository.getCompletedTasks();
        tasksWithReminders = repository.getTasksWithReminders();

        // 初始化过滤参数
        filterCategory.setValue(null);
        filterPriority.setValue(null);
        searchQuery.setValue("");
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

    // 获取带提醒的任务
    public LiveData<List<Task>> getTasksWithReminders() {
        return tasksWithReminders;
    }

    // 按类别筛选任务
    public LiveData<List<Task>> getTasksByCategory() {
        return Transformations.switchMap(filterCategory, category -> {
            if (category != null && !category.isEmpty()) {
                return repository.getTasksByCategory(category);
            } else {
                return allTasks;
            }
        });
    }

    // 按优先级筛选任务
    public LiveData<List<Task>> getTasksByPriority() {
        return Transformations.switchMap(filterPriority, priority -> {
            if (priority != null) {
                return repository.getTasksByPriority(priority);
            } else {
                return allTasks;
            }
        });
    }

    // 搜索任务
    public LiveData<List<Task>> searchTasks() {
        return Transformations.switchMap(searchQuery, query -> {
            if (query != null && !query.isEmpty()) {
                return repository.searchTasks(query);
            } else {
                return allTasks;
            }
        });
    }

    // 获取今天的任务
    public LiveData<List<Task>> getTodayTasks() {
        Date startOfDay = getStartOfDay(new Date());
        Date endOfDay = getEndOfDay(new Date());
        return repository.getTasksByDueDate(startOfDay, endOfDay);
    }

    // 获取未来7天的任务
    public LiveData<List<Task>> getWeekTasks() {
        Date startOfDay = getStartOfDay(new Date());
        Date endOfWeek = addDays(startOfDay, 7);
        return repository.getTasksByDueDate(startOfDay, endOfWeek);
    }

    // 插入任务
    public void insert(Task task) {
        repository.insert(task);

        // 如果任务有提醒，设置提醒
        if (task.getReminderTime() != null && !task.isCompleted()) {
            ReminderManager.scheduleReminder(getApplication(), task);
        }
    }

    // 更新任务
    public void update(Task task) {
        repository.update(task);

        // 更新任务的提醒
        if (task.getId() > 0) {
            // 先取消旧的提醒
            ReminderManager.cancelReminder(getApplication(), task.getId());

            // 如果有新的提醒时间并且任务未完成，设置新的提醒
            if (task.getReminderTime() != null && !task.isCompleted()) {
                ReminderManager.scheduleReminder(getApplication(), task);
            }
        }
    }

    // 删除任务
    public void delete(Task task) {
        // 删除任务前取消提醒
        if (task.getReminderTime() != null) {
            ReminderManager.cancelReminder(getApplication(), task.getId());
        }
        repository.delete(task);
    }

    // 删除所有任务
    public void deleteAllTasks() {
        // 这里可能需要遍历所有任务并取消提醒
        // 为简化实现，这里省略了该步骤
        repository.deleteAllTasks();
    }

    // 标记任务为已完成
    public void completeTask(Task task) {
        task.setCompleted(true);
        // 任务完成后取消提醒
        if (task.getReminderTime() != null) {
            ReminderManager.cancelReminder(getApplication(), task.getId());
        }
        repository.update(task);
    }

    // 标记任务为未完成
    public void uncompleteTask(Task task) {
        task.setCompleted(false);
        // 如果任务未完成且有提醒时间，重新设置提醒
        if (task.getReminderTime() != null) {
            ReminderManager.scheduleReminder(getApplication(), task);
        }
        repository.update(task);
    }

    // 切换任务完成状态
    public void toggleTaskCompleted(Task task) {
        boolean newCompletedState = !task.isCompleted();
        task.setCompleted(newCompletedState);

        // 根据完成状态处理提醒
        if (task.getReminderTime() != null) {
            if (newCompletedState) {
                // 任务完成，取消提醒
                ReminderManager.cancelReminder(getApplication(), task.getId());
            } else {
                // 任务未完成，重新设置提醒
                ReminderManager.scheduleReminder(getApplication(), task);
            }
        }
        repository.update(task);
    }

    // 设置筛选类别
    public void setFilterCategory(String category) {
        filterCategory.setValue(category);
    }

    // 设置筛选优先级
    public void setFilterPriority(Integer priority) {
        filterPriority.setValue(priority);
    }

    // 设置搜索查询
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    // 设置选中的任务
    public void selectTask(Task task) {
        selectedTask.setValue(task);
    }

    // 获取选中的任务
    public LiveData<Task> getSelectedTask() {
        return selectedTask;
    }

    // 以下是DateTimeUtils方法的实现，替代原来的引用
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
}
