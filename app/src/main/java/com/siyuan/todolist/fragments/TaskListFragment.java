package com.siyuan.todolist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.siyuan.todolist.R;
import com.siyuan.todolist.activities.TaskDetailActivity;
import com.siyuan.todolist.adapters.TaskAdapter;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.utils.SwipeToDeleteCallback;
import com.siyuan.todolist.viewmodels.TaskViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class TaskListFragment extends Fragment implements SwipeToDeleteCallback.OnSwipeListener {
    
    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private MaterialCardView emptyStateCard;
    
    // 标识当前Fragment展示的是哪种任务列表
    public static final int TYPE_ALL = 0;
    public static final int TYPE_TODAY = 1;
    public static final int TYPE_COMPLETED = 2;
    
    private int listType;
    
    public static TaskListFragment newInstance(int listType) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("list_type", listType);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listType = getArguments().getInt("list_type", TYPE_ALL);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyStateCard = view.findViewById(R.id.emptyStateCard);
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        
        // 设置适配器
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);
        
        // 添加滑动删除功能
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(requireContext(), this, adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // 获取ViewModel
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        
        // 设置点击事件
        adapter.setOnItemClickListener(task -> {
            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
            intent.putExtra("task", task);
            startActivity(intent);
        });
        
        // 设置任务完成状态变化监听
        adapter.setOnTaskCheckedChangeListener((task, isChecked) -> {
            task.setCompleted(isChecked);
            taskViewModel.update(task);
        });
        
        // 根据列表类型订阅不同的LiveData
        switch (listType) {
            case TYPE_TODAY:
                taskViewModel.getTodayTasks().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                break;
            case TYPE_COMPLETED:
                taskViewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                break;
            case TYPE_ALL:
            default:
                // 当有筛选条件时，订阅筛选后的任务列表
                taskViewModel.getTasksByCategory().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                
                taskViewModel.getTasksByPriority().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                
                taskViewModel.searchTasks().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                
                // 默认显示所有任务
                taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
                    updateTaskList(tasks);
                });
                break;
        }
    }
    
    private void updateTaskList(List<Task> tasks) {
        adapter.submitList(tasks);
        
        // 显示或隐藏空状态视图
        if (tasks == null || tasks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateCard.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateCard.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onSwiped(int position) {
        Task task = adapter.getTaskAt(position);
        taskViewModel.delete(task);
        
        Snackbar.make(recyclerView, "任务已删除", Snackbar.LENGTH_LONG)
                .setAction("撤销", v -> taskViewModel.insert(task))
                .show();
    }
}
