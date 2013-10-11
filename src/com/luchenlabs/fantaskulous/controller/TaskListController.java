package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListController {

    private final TaskList _taskList;

    private TaskController[] tasks;

    public TaskListController(TaskList list) {
        _taskList = list;
    }

    public void addTask(CharSequence description) {
        Task task = new Task(_taskList, description.toString());
        _taskList.addTask(task);
    }
}
