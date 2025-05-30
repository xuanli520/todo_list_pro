package com.siyuan.todolist.activities;

import com.siyuan.todolist.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import androidx.viewpager2.widget.ViewPager2;

import com.siyuan.todolist.adapters.TaskViewPagerAdapter;
import com.siyuan.todolist.models.Task;
import com.siyuan.todolist.viewmodels.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;

    private TaskViewModel taskViewModel;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // 初始化ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // 初始化视图
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabAddTask = findViewById(R.id.fabAddTask);

        // 设置ViewPager适配器
        TaskViewPagerAdapter pagerAdapter = new TaskViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 连接TabLayout和ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("所有任务");
                    break;
                case 1:
                    tab.setText("今日任务");
                    break;
                case 2:
                    tab.setText("已完成");
                    break;
            }
        }).attach();

        // 添加任务按钮点击事件
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // 设置搜索功能
        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskViewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskViewModel.setSearchQuery(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_date) {
            // 按日期排序，已经在DAO层实现
            return true;
        } else if (id == R.id.action_sort_priority) {
            taskViewModel.setFilterPriority(null); // 重置过滤条件
            // 切换到按优先级排序的LiveData
            return true;
        } else if (id == R.id.action_filter_high) {
            taskViewModel.setFilterPriority(3); // 高优先级
            return true;
        } else if (id == R.id.action_filter_medium) {
            taskViewModel.setFilterPriority(2); // 中优先级
            return true;
        } else if (id == R.id.action_filter_low) {
            taskViewModel.setFilterPriority(1); // 低优先级
            return true;
        } else if (id == R.id.action_filter_work) {
            taskViewModel.setFilterCategory("工作");
            return true;
        } else if (id == R.id.action_filter_personal) {
            taskViewModel.setFilterCategory("个人");
            return true;
        } else if (id == R.id.action_filter_study) {
            taskViewModel.setFilterCategory("学习");
            return true;
        } else if (id == R.id.action_filter_shopping) {
            taskViewModel.setFilterCategory("购物");
            return true;
        } else if (id == R.id.action_clear_filters) {
            taskViewModel.setFilterCategory(null);
            taskViewModel.setFilterPriority(null);
            taskViewModel.setSearchQuery("");
            return true;
        } else if (id == R.id.action_settings) {
            // 跳转到设置页面
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_TASK_REQUEST && data != null) {
                Task task = (Task) data.getSerializableExtra("task");
                if (task != null) {
                    taskViewModel.insert(task);
                    Snackbar.make(viewPager, "任务已添加", Snackbar.LENGTH_SHORT).show();
                }
            } else if (requestCode == EDIT_TASK_REQUEST && data != null) {
                Task task = (Task) data.getSerializableExtra("task");
                int id = data.getIntExtra("task_id", -1);

                if (id == -1) {
                    Snackbar.make(viewPager, "任务无法更新", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                task.setId(id);
                taskViewModel.update(task);
                Snackbar.make(viewPager, "任务已更新", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    // ViewPager的Fragment类在这里实现
    // 或者单独创建Fragment类文件
}
