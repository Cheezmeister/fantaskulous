/**
 * 
 */
package com.luchenlabs.fantaskulous.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

/**
 * @author cheezmeister
 * 
 */
public class TaskListView extends RelativeLayout implements Observer {

    private TaskList _taskList;
    private TaskListController _controller;

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setTaskList(TaskList taskList) {
        if (_taskList != null) {
            _taskList.deleteObserver(this);
        }
        _taskList = taskList;
        _controller = new TaskListController(_taskList);
        _taskList.addObserver(this);
        // hookListeners();

        TaskArrayAdapter adapter = new TaskArrayAdapter(getContext(), R.layout.view_task, 0, taskList);

        final TaskListListView listView = (TaskListListView) findViewById(R.id.taskListListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO What do we do here, mmm? Drill into task, methinks.
            }
        });
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (_taskList != arg0)
            return;

        // TODO abstract sorting
        Comparator<Task> comparator = new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                int priComp = lhs.getPriority().compareTo(rhs.getPriority());
                if (priComp != 0)
                    return priComp;
                return lhs.getDate().compareTo(rhs.getDate());
            }

        };
        Collections.sort(_taskList.getTasks(), comparator);
    }

    protected TaskListController getController() {
        return _controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hookListeners();
    }

    private void hookListeners() {
        final TaskListListView taskListView = (TaskListListView) findViewById(R.id.taskListListView);

        final EditText fieldDescription = (EditText) findViewById(R.id.fieldDesc);

        fieldDescription.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    getController().addTask(fieldDescription.getText());
                    fieldDescription.clearComposingText();
                }
                return false;
            }
        });

    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_tasklist, this, true);
    }

}
