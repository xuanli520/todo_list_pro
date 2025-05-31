package com.siyuan.todolist.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.siyuan.todolist.R;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.siyuan.todolist.utils.ReminderManager;
import com.siyuan.todolist.viewmodels.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class TaskEditActivity extends AppCompatActivity {

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private Button dueDateButton;
    private Button reminderTimeButton;
    private SwitchMaterial reminderSwitch;
    private Spinner prioritySpinner;
    private Spinner categorySpinner;
    private MaterialButton saveButton;

    private TaskViewModel taskViewModel;
    private Task currentTask;
    private int taskId = -1;

    private Date dueDate;
    private Date reminderTime;
    private Calendar reminderCalendar = Calendar.getInstance();
    private boolean isReminderEnabled = false;

    private String[] priorityLabels;
    private int[] priorityValues = {1, 2, 3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        editTextTitle = findViewById(R.id.titleEditText);
        editTextDescription = findViewById(R.id.descriptionEditText);
        dueDateButton = findViewById(R.id.dueDateButton);
        reminderTimeButton = findViewById(R.id.reminderTimeButton);
        reminderSwitch = findViewById(R.id.reminderSwitch);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);

        priorityLabels = getResources().getStringArray(R.array.priority_array);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            currentTask = (Task) intent.getSerializableExtra("task");
            if (currentTask != null) {
                taskId = currentTask.getId();
                populateTaskData();
            }
        } else {
            dueDateButton.setText(R.string.select_due_date);
            reminderTimeButton.setText(R.string.select_reminder_time);
            updateReminderButtonState();
        }

        dueDateButton.setOnClickListener(v -> showDatePickerDialog());
        reminderTimeButton.setOnClickListener(v -> showTimePickerDialog());

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isReminderEnabled = isChecked;
            updateReminderButtonState();
        });

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void updateReminderButtonState() {
        reminderTimeButton.setEnabled(isReminderEnabled);
        reminderTimeButton.setAlpha(isReminderEnabled ? 1.0f : 0.5f);
    }

    private void populateTaskData() {
        if (currentTask == null) return;

        editTextTitle.setText(currentTask.getTitle());
        editTextDescription.setText(currentTask.getDescription());

        if (currentTask.getDueDate() != null) {
            dueDate = currentTask.getDueDate();
            dueDateButton.setText(DateTimeUtils.formatDate(dueDate));
        } else {
            dueDateButton.setText(R.string.select_due_date);
        }

        if (currentTask.getReminderTime() != null) {
            reminderTime = currentTask.getReminderTime();
            reminderCalendar.setTime(reminderTime);
            reminderTimeButton.setText(DateTimeUtils.formatTime(reminderTime));
            reminderSwitch.setChecked(true);
            isReminderEnabled = true;
        } else {
            reminderTimeButton.setText(R.string.select_reminder_time);
            reminderSwitch.setChecked(false);
            isReminderEnabled = false;
        }
        updateReminderButtonState();

        int priority = currentTask.getPriority();
        String priorityLabel = "";
        switch (priority) {
            case 1:
                priorityLabel = priorityLabels[0];
                break;
            case 3:
                priorityLabel = priorityLabels[2];
                break;
            default:
                priorityLabel = priorityLabels[1];
                break;
        }

        for (int i = 0; i < prioritySpinner.getCount(); i++) {
            if (prioritySpinner.getItemAtPosition(i).toString().equals(priorityLabel)) {
                prioritySpinner.setSelection(i);
                break;
            }
        }

        String category = currentTask.getCategory();
        if (category != null && !category.isEmpty()) {
            for (int i = 0; i < categorySpinner.getCount(); i++) {
                if (categorySpinner.getItemAtPosition(i).toString().equals(category)) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (dueDate != null) {
            calendar.setTime(dueDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dueDate = calendar.getTime();
                    dueDateButton.setText(DateTimeUtils.formatDate(dueDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (reminderTime != null) {
            calendar.setTime(reminderTime);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderCalendar.set(Calendar.MINUTE, minute);
                    reminderCalendar.set(Calendar.SECOND, 0);

                    reminderTime = reminderCalendar.getTime();
                    reminderTimeButton.setText(DateTimeUtils.formatTime(reminderTime));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Snackbar.make(saveButton, "请输入任务标题", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (dueDate == null) {
            Snackbar.make(saveButton, "请选择截止日期", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int selectedPriorityIndex = prioritySpinner.getSelectedItemPosition();
        int priority = priorityValues[selectedPriorityIndex];
        String category = categorySpinner.getSelectedItem().toString();

        // 确定是否设置提醒时间
        Date finalReminderTime = null;
        if (isReminderEnabled && reminderTime != null) {
            finalReminderTime = reminderTime;
        }

        Task task = new Task(title, description, dueDate, finalReminderTime, priority, category);

        if (taskId == -1) {
            task.setCreatedDate(new Date());
            taskViewModel.insert(task);
            Snackbar.make(saveButton, "任务已添加", Snackbar.LENGTH_SHORT).show();
        } else {
            task.setId(taskId);
            task.setCreatedDate(currentTask.getCreatedDate());
            task.setCompleted(currentTask.isCompleted());

            // 如果存在旧的提醒，取消它
            if (currentTask.getReminderTime() != null) {
                ReminderManager.cancelReminder(this, taskId);
            }

            taskViewModel.update(task);
            Snackbar.make(saveButton, "任务已更新", Snackbar.LENGTH_SHORT).show();
        }

        // 如果启用了提醒，设置新的提醒
        if (isReminderEnabled && finalReminderTime != null) {
            if (taskId == -1) {
                // 对于新任务，需要在数据库中查询新生成的ID
                // 这里假设ReminderManager可以处理这种情况
                ReminderManager.scheduleReminder(this, task);
            } else {
                ReminderManager.scheduleReminder(this, task);
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);
        if (taskId != -1) {
            resultIntent.putExtra("task_id", taskId);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
