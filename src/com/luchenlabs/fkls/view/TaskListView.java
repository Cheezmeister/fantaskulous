/**
 *
 */
package com.luchenlabs.fkls.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.controller.TaskListController;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;

/**
 * @author cheezmeister
 *
 */
public class TaskListView extends RelativeLayout implements Observer, FView<TaskList> {

    private TaskList _taskList;
    private TaskListController _controller;
    private ListView _listView;

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskListView(Context context, AttributeSet attrs, int defStyle) {
        // SO // yeah. // Metacognition. // Again. // Note // to // self: // go
        // // slow // and // don't // fuck // up. // That // goes // for // you
        // // too, // hands. // #saidaustinpowers
        super(context, attrs, defStyle);
        init(context);
    }

    protected TaskListController getController() {
        return _controller;
    }

    /**
     * Grab the global task controller and stash it in a field
     */
    private void grabController() {
        _controller = G.getState().getTaskListController();
    }

    private void hookListeners() {
        final EditText fieldDescription = (EditText) findViewById(R.id.fieldDesc);

        fieldDescription.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(android.view.View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        fieldDescription.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                CharSequence text = fieldDescription.getText();
                if (text.length() == 0)
                return false;

                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    getController().addTask(G.getState().getModel(), _taskList, text);
                    fieldDescription.clearComposingText();
                    fieldDescription.setText(null);

                    // here be metacognition. // Make it fancy words. //
                    // Code is my diary. This is // kind of cool. Especially
                    // since it might actually // be used. Right at my
                    // fingertips, got no // excuse. Vroop!
                    // BingletonBingletonBingletonBingleton! // Lulz. This is
                    // going to be
                    // one long-ass column when // I"m through with it
                    // AGAIN. So metacognition, // is just about what it
                    // sounds like. I shall // google it! WTF? Back from
                    // food. Sans milk :( Oh // well. Tiem to coad
                }
                return false;
            }
        });
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_tasklist, this, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        unobserve();
        super.onDetachedFromWindow();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void refresh() {
        TaskArrayAdapter adapter = (TaskArrayAdapter) _listView.getAdapter();
        _listView.invalidate();
        if (adapter == null) {
            adapter = new TaskArrayAdapter(getContext(), _taskList, _controller);
            _listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.refresh();
    }

    @Override
    public void setModel(TaskList model) {
        if (_taskList != null) {
            _taskList.deleteObserver(this);
        }
        _taskList = model;
        grabController();
        _taskList.addObserver(this);
        for (Task task : _taskList.getTasks()) {
            task.addObserver(this);
        }

        _listView = (ListView) findViewById(R.id.taskListListView);
        _listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        _listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _listView.setSelection(position);
            }
        });
        hookListeners();
        refresh();
    }

    private void unobserve() {
        if (_taskList != null) {
            _taskList.deleteObserver(this);
            for (Task task : _taskList.getTasks()) {
                task.deleteObserver(this);
            }
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        ((TaskArrayAdapter) _listView.getAdapter()).refresh();
    }

}
