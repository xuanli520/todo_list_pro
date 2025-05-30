package com.siyuan.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.siyuan.todolist.R;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.siyuan.todolist.viewmodels.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private Task currentTask;

    private TextView textViewTaskTitle;
    private TextView textViewTaskDescription;
    private TextView textViewCreatedDate;
    private TextView textViewReminderTime;
    private Chip chipCategory;
    private Chip chipPriority;
    private Chip chipDueDate;
    private MaterialButton buttonComplete;
    private MaterialButton buttonEdit;
    private MaterialButton buttonDelete;
    private FloatingActionButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // 初始化ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // 初始化视图
        textViewTaskTitle = findViewById(R.id.textViewTaskTitle);
        textViewTaskDescription = findViewById(R.id.textViewTaskDescription);
        textViewCreatedDate = findViewById(R.id.textViewCreatedDate);
        textViewReminderTime = findViewById(R.id.textViewReminderTime);
        chipCategory = findViewById(R.id.chipCategory);
        chipPriority = findViewById(R.id.chipPriority);
        chipDueDate = findViewById(R.id.chipDueDate);
        buttonComplete = findViewById(R.id.buttonComplete);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonBack = findViewById(R.id.buttonBack);

        // 设置返回按钮点击事件
        buttonBack.setOnClickListener(v -> onBackPressed());

        // 获取传入的任务对象
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            currentTask = (Task) intent.getSerializableExtra("task");
            displayTaskDetails();
        } else {
            finish();
        }

        // 设置按钮点击事件
        buttonComplete.setOnClickListener(v -> {
            boolean wasCompleted = currentTask.isCompleted();
            currentTask.setCompleted(!wasCompleted);
            taskViewModel.update(currentTask);

            // 更新UI
            updateCompleteButtonState();

            String message = currentTask.isCompleted() ? "任务已标记为完成" : "任务已标记为未完成";
            Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
        });

        buttonEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(TaskDetailActivity.this, TaskEditActivity.class);
            editIntent.putExtra("task", currentTask);
            startActivityForResult(editIntent, MainActivity.EDIT_TASK_REQUEST);
        });

        buttonDelete.setOnClickListener(v -> {
            deleteTask();
            Snackbar.make(v, "任务已删除", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void displayTaskDetails() {
        if (currentTask == null) return;

        // 设置标题和描述
        textViewTaskTitle.setText(currentTask.getTitle());
        textViewTaskDescription.setText(currentTask.getDescription());

        // 设置创建日期
        if (currentTask.getCreatedDate() != null) {
            textViewCreatedDate.setText(DateTimeUtils.formatDate(currentTask.getCreatedDate()));
        } else {
            textViewCreatedDate.setText("未知");
        }

        // 设置提醒时间
        if (currentTask.getReminderTime() != null) {
            textViewReminderTime.setText(DateTimeUtils.formatDateTime(currentTask.getReminderTime()));
        } else {
            textViewReminderTime.setText("未设置");
        }

        // 设置类别
        if (currentTask.getCategory() != null && !currentTask.getCategory().isEmpty()) {
            chipCategory.setText(currentTask.getCategory());
            chipCategory.setVisibility(View.VISIBLE);
        } else {
            chipCategory.setVisibility(View.GONE);
        }

        // 设置优先级
        String priorityText;
        int priorityColor;
        switch (currentTask.getPriority()) {
            case 1:
                priorityText = "低优先级";
                priorityColor = getResources().getColor(R.color.task_priority_low);
                break;
            case 3:
                priorityText = "高优先级";
                priorityColor = getResources().getColor(R.color.task_priority_high);
                break;
            default:
                priorityText = "中优先级";
                priorityColor = getResources().getColor(R.color.task_priority_medium);
                break;
        }
        chipPriority.setText(priorityText);
        chipPriority.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(priorityColor));

        // 设置截止日期
        if (currentTask.getDueDate() != null) {
            String dueDateText = DateTimeUtils.getFriendlyDateString(currentTask.getDueDate()) + "截止";
            chipDueDate.setText(dueDateText);
            chipDueDate.setVisibility(View.VISIBLE);
        } else {
            chipDueDate.setVisibility(View.GONE);
        }

        // 更新完成按钮状态
        updateCompleteButtonState();
    }

    private void updateCompleteButtonState() {
        if (currentTask.isCompleted()) {
            buttonComplete.setText("标记为未完成");
            buttonComplete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.task_completed)));
        } else {
            buttonComplete.setText("标记为已完成");
            buttonComplete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        }
    }

    private void deleteTask() {
        taskViewModel.delete(currentTask);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.EDIT_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            Task updatedTask = (Task) data.getSerializableExtra("task");
            int id = data.getIntExtra("task_id", -1);

            if (id != -1) {
                updatedTask.setId(id);
                currentTask = updatedTask;
                displayTaskDetails();
            }
        }
    }
}
