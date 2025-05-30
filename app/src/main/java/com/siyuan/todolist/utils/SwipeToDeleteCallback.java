package com.siyuan.todolist.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.siyuan.todolist.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    
    private final Drawable deleteIcon;
    private final ColorDrawable background;
    private final int iconMargin;
    private final OnSwipeListener listener;
    
    public interface OnSwipeListener {
        void onSwiped(int position);
    }

    public SwipeToDeleteCallback(Context context, OnSwipeListener listener, RecyclerView.Adapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.listener = listener;
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.parseColor("#F44336"));
        iconMargin = 16;
    }
    
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
    
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        listener.onSwiped(position);
    }
    
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        
        // 不在用户未移动时绘制背景
        if (dX == 0 && !isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
            return;
        }
        
        // 设置背景
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                itemView.getRight(), itemView.getBottom());
        background.draw(c);
        
        // 计算图标位置
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;
        
        // 设置图标边界并绘制
        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(c);
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
