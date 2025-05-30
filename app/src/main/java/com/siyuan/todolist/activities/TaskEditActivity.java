package com.siyuan.todolist.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.siyuan.todolist.R;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class TaskEditActivity extends AppCompatActivity {

    private TextInputEditText editTextTitle, editTextDescription;
    private TextView textViewDate, textViewTime;
    private MaterialCardView datePickerCard, timePickerCard;
    private RadioButton radioPriorityLow, radioPriorityMedium, radioPriorityHigh;
    private ChipGroup categoryChipGroup;
    private SwitchMaterial switchReminder;
    private LinearLayout reminderLayout;
    private Button buttonSave;
    
    private Date selectedDueDate = null;
    private Date selectedReminderTime = null;
    private int selectedPriority = 2; // 默认中优先级
    private String selectedCategory = "";
    
    private boolean isEditMode = false;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        
        // 初始化视图
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        datePickerCard = findViewById(R.id.datePickerCard);
        timePickerCard = findViewById(R.id.timePickerCard);
        radioPriorityLow = findViewById(R.id.radioPriorityLow);
        radioPriorityMedium = findViewById(R.id.radioPriorityMedium);
        radioPriorityHigh = findViewById(R.id.radioPriorityHigh);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        switchReminder = findViewById(R.id.switchReminder);
        reminderLayout = findViewById(R.id.reminderLayout);
        buttonSave = findViewById(R.id.buttonSave);
        
        // 检查是编辑模式还是添加模式
        Intent intent = getIntent();
        if (intent.hasExtra("task")) {
            isEditMode = true;
            Task task = (Task) intent.getSerializableExtra("task");
            taskId = task.getId();
            
            // 填充表单数据
            editTextTitle.setText(task.getTitle());
            editTextDescription.setText(task.getDescription());
            
            selectedDueDate = task.getDueDate();
            if (selectedDueDate != null) {
                textViewDate.setText(DateTimeUtils.formatDate(selectedDueDate));
            }
            
            selectedReminderTime = task.getReminderTime();
            if (selectedReminderTime != null) {
                switchReminder.setChecked(true);
                reminderLayout.setVisibility(View.VISIBLE);
                textViewTime.setText(DateTimeUtils.formatTime(selectedReminderTime));
            }
            
            // 设置优先级
            selectedPriority = task.getPriority();
            switch (selectedPriority) {
                case 1:
                    radioPriorityLow.setChecked(true);
                    break;
                case 3:
                    radioPriorityHigh.setChecked(true);
                    break;
                default:
                    radioPriorityMedium.setChecked(true);
                    break;
            }
            
            // 设置类别
            selectedCategory = task.getCategory();
            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
                    View child = categoryChipGroup.getChildAt(i);
                    if (child instanceof Chip) {
                        Chip chip = (Chip) child;
                        if (chip.getText().toString().equals(selectedCategory)) {
                            chip.setChecked(true);
                            break;
                        }
                    }
                }
            }
            
            // 更新UI标题
            getSupportActionBar().setTitle("编辑任务");
            buttonSave.setText("更新任务");
        } else {
            getSupportActionBar().setTitle("添加任务");
            buttonSave.setText("添加任务");
        }
        
        // 设置日期选择器
        datePickerCard.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            
            if (selectedDueDate != null) {
                c.setTime(selectedDueDate);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TaskEditActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        selectedDueDate = calendar.getTime();
                        textViewDate.setText(DateTimeUtils.formatDate(selectedDueDate));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
        
        // 设置时间选择器
        timePickerCard.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            
            if (selectedReminderTime != null) {
                c.setTime(selectedReminderTime);
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }
            
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    TaskEditActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        Calendar calendar = Calendar.getInstance();
                        if (selectedDueDate != null) {
                            calendar.setTime(selectedDueDate);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        selectedReminderTime = calendar.getTime();
                        textViewTime.setText(DateTimeUtils.formatTime(selectedReminderTime));
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });
        
        // 优先级选择
        RadioGroup priorityRadioGroup = findViewById(R.id.priorityRadioGroup);
        priorityRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPriorityLow) {
                selectedPriority = 1;
            } else if (checkedId == R.id.radioPriorityHigh) {
                selectedPriority = 3;
            } else {
                selectedPriority = 2;
            }
        });
        
        // 类别选择
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                Chip chip = findViewById(checkedId);
                if (chip != null && checkedId != R.id.chipAddCategory) {
                    selectedCategory = chip.getText().toString();
                }
            } else {
                selectedCategory = "";
            }
        });
        
        // 添加新类别
        Chip chipAddCategory = findViewById(R.id.chipAddCategory);
        chipAddCategory.setOnClickListener(v -> {
            // 这里可以弹出对话框让用户输入新的类别
            // 简化起见，这里省略了该功能的实现
        });
        
        // 提醒开关
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                selectedReminderTime = null;
            }
        });
        
        // 保存按钮
        buttonSave.setOnClickListener(v -> saveTask());
    }
    
    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        
        // 验证表单
        if (title.isEmpty()) {
            editTextTitle.setError("请输入任务标题");
            return;
        }
        
        // 创建任务对象
        Task task = new Task(title, description, selectedDueDate, 
                switchReminder.isChecked() ? selectedReminderTime : null, 
                selectedPriority, selectedCategory);
        
        // 返回结果
        Intent data = new Intent();
        data.putExtra("task", task);
        
        if (isEditMode) {
            data.putExtra("task_id", taskId);
        }
        
        setResult(RESULT_OK, data);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
