package com.luchenlabs.fantaskulous.controller;

import java.util.ArrayList;
import java.util.List;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListController {

    private final TaskList _taskList;

    private final List<TaskController> _tasks;

    public TaskListController(TaskList list) {
        _taskList = list;
        _tasks = new ArrayList<TaskController>(list.getTasks().size());
        for (Task task : list.getTasks()) {
            _tasks.add(new TaskController(this, task));
        }
    }

    public void addTask(CharSequence description) {
        Task task = new Task(_taskList, description.toString());
        _taskList.addTask(task);
    }
}
