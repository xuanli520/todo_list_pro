package com.siyuan.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.siyuan.todolist.R;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.siyuan.todolist.viewmodels.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private Task currentTask;

    private TextView taskTitle;
    private TextView taskDescription;
    private TextView taskDueDate;
    private TextView taskPriority;
    private TextView taskCategory;
    private MaterialButton editTaskBtn;
    private MaterialButton deleteTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // 初始化ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // 修正：使用正确的ID查找视图
        taskTitle = findViewById(R.id.task_title);
        taskDescription = findViewById(R.id.task_description);
        taskDueDate = findViewById(R.id.task_due_date);
        taskPriority = findViewById(R.id.task_priority);
        taskCategory = findViewById(R.id.task_category);
        editTaskBtn = findViewById(R.id.edit_task_btn);
        deleteTaskBtn = findViewById(R.id.delete_task_btn);

        // 获取传入的任务对象
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            currentTask = (Task) intent.getSerializableExtra("task");
            displayTaskDetails();
        } else {
            finish(); // 如果没有任务对象，则关闭此Activity
        }

        // 设置编辑按钮点击事件
        editTaskBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(TaskDetailActivity.this, TaskEditActivity.class);
            editIntent.putExtra("task", currentTask);
            startActivityForResult(editIntent, MainActivity.EDIT_TASK_REQUEST);
        });

        // 设置删除按钮点击事件
        deleteTaskBtn.setOnClickListener(v -> {
            deleteTask();
            Snackbar.make(v, "任务已删除", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void displayTaskDetails() {
        if (currentTask == null) return;

        // 添加空值检查防止崩溃
        if (taskTitle != null) {
            taskTitle.setText(currentTask.getTitle());
        }

        if (taskDescription != null) {
            taskDescription.setText(currentTask.getDescription());
        }

        // 设置截止日期
        if (taskDueDate != null) {
            if (currentTask.getDueDate() != null) {
                taskDueDate.setText(DateTimeUtils.formatDate(currentTask.getDueDate()));
            } else {
                taskDueDate.setText("未设置");
            }
        }

        // 设置优先级
        if (taskPriority != null) {
            String priorityText;
            switch (currentTask.getPriority()) {
                case 1:
                    priorityText = "低优先级";
                    break;
                case 3:
                    priorityText = "高优先级";
                    break;
                default:
                    priorityText = "中优先级";
                    break;
            }
            taskPriority.setText(priorityText);
        }

        // 设置类别
        if (taskCategory != null) {
            taskCategory.setText(currentTask.getCategory());
        }
    }

    private void deleteTask() {
        taskViewModel.delete(currentTask);
        finish(); // 删除后关闭此Activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.EDIT_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            Task updatedTask = (Task) data.getSerializableExtra("task");
            currentTask = updatedTask;
            displayTaskDetails();
        }
    }
}
