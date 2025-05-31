package com.siyuan.todolist.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.siyuan.todolist.R;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.DateTimeUtils;
import com.google.android.material.chip.Chip;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private OnItemClickListener listener;
    private OnTaskCheckedChangeListener checkedChangeListener;

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.isCompleted() == newItem.isCompleted() &&
                    oldItem.getPriority() == newItem.getPriority() &&
                    ((oldItem.getDueDate() == null && newItem.getDueDate() == null) ||
                            (oldItem.getDueDate() != null && newItem.getDueDate() != null &&
                                    oldItem.getDueDate().equals(newItem.getDueDate()))) &&
                    ((oldItem.getReminderTime() == null && newItem.getReminderTime() == null) ||
                            (oldItem.getReminderTime() != null && newItem.getReminderTime() != null &&
                                    oldItem.getReminderTime().equals(newItem.getReminderTime())));
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);

        holder.textTitle.setText(currentTask.getTitle());
        holder.textDescription.setText(currentTask.getDescription());

        // 设置任务优先级指示器颜色
        int priorityColor;
        switch (currentTask.getPriority()) {
            case 1: // 低
                priorityColor = holder.itemView.getContext().getResources().getColor(R.color.task_priority_low);
                break;
            case 3: // 高
                priorityColor = holder.itemView.getContext().getResources().getColor(R.color.task_priority_high);
                break;
            default: // 中
                priorityColor = holder.itemView.getContext().getResources().getColor(R.color.task_priority_medium);
                break;
        }
        holder.priorityIndicator.setBackgroundColor(priorityColor);

        // 设置截止日期
        if (currentTask.getDueDate() != null) {
            holder.textDueDate.setVisibility(View.VISIBLE);
            String dueDateText = DateTimeUtils.getFriendlyDateString(currentTask.getDueDate());
            holder.textDueDate.setText(dueDateText);

            // 如果已过期且未完成，显示红色
            if (DateTimeUtils.isOverdue(currentTask.getDueDate()) && !currentTask.isCompleted()) {
                holder.textDueDate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error));
            } else {
                holder.textDueDate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.on_background));
            }
        } else {
            holder.textDueDate.setVisibility(View.GONE);
        }

        // 设置提醒图标
        if (currentTask.getReminderTime() != null) {
            holder.reminderIcon.setVisibility(View.VISIBLE);
            // 可选：显示提醒时间提示
            holder.reminderIcon.setContentDescription("提醒时间: " +
                    DateTimeUtils.formatTime(currentTask.getReminderTime()));
        } else {
            holder.reminderIcon.setVisibility(View.GONE);
        }

        // 设置类别
        if (currentTask.getCategory() != null && !currentTask.getCategory().isEmpty()) {
            holder.chipCategory.setVisibility(View.VISIBLE);
            holder.chipCategory.setText(currentTask.getCategory());
        } else {
            holder.chipCategory.setVisibility(View.GONE);
        }

        // 设置完成状态
        holder.checkboxComplete.setChecked(currentTask.isCompleted());

        // 任务完成时，标题添加删除线
        if (currentTask.isCompleted()) {
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textTitle.setAlpha(0.6f);
            holder.textDescription.setAlpha(0.6f);
            if (holder.reminderIcon.getVisibility() == View.VISIBLE) {
                holder.reminderIcon.setAlpha(0.6f);
            }
        } else {
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textTitle.setAlpha(1.0f);
            holder.textDescription.setAlpha(1.0f);
            holder.reminderIcon.setAlpha(1.0f);
        }

        // 设置复选框变化监听
        holder.checkboxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkedChangeListener != null && buttonView.isPressed()) {
                checkedChangeListener.onTaskCheckedChange(getItem(holder.getAdapterPosition()), isChecked);
            }
        });
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textDescription;
        private TextView textDueDate;
        private CheckBox checkboxComplete;
        private View priorityIndicator;
        private Chip chipCategory;
        private ImageView reminderIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textDueDate = itemView.findViewById(R.id.textDueDate);
            checkboxComplete = itemView.findViewById(R.id.checkboxComplete);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            reminderIcon = itemView.findViewById(R.id.reminderIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnTaskCheckedChangeListener {
        void onTaskCheckedChange(Task task, boolean isChecked);
    }

    public void setOnTaskCheckedChangeListener(OnTaskCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }
}
