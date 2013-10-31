package com.luchenlabs.fantaskulous.controller;

import java.util.List;

import com.luchenlabs.fantaskulous.model.TaskList;

public class MainController {
    private final List<TaskList> _taskLists;

    public MainController(List<TaskList> taskLists) {
        this._taskLists = taskLists;
    }

    public TaskList createTaskList(CharSequence title) {
        TaskList list = new TaskList(title.toString());
        _taskLists.add(list);
        return list;
    }

    public boolean removeTaskList(CharSequence name) {
        for (TaskList task : _taskLists) {
            if (task.getName().equals(name))
                return _taskLists.remove(task);
        }
        return false;
    }
}
