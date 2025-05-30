package com.siyuan.todolist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.siyuan.todolist.fragments.TaskListFragment;

public class TaskViewPagerAdapter extends FragmentStateAdapter {

    public TaskViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create appropriate fragment based on position
        switch (position) {
            case 0:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_ALL);
            case 1:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_TODAY);
            case 2:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_COMPLETED);
            default:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_ALL);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 3 tabs: All, Today, Completed
    }
}
