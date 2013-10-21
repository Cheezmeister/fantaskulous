package com.luchenlabs.fantaskulous.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListListView extends ListView {

    private TaskList _taskList;

    public TaskListListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskListListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
