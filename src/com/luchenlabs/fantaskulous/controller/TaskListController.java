package com.luchenlabs.fantaskulous.controller;

import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListController {

    private final TaskList _taskList;

    private final HashMap<UUID, TaskController> _children;

    public TaskListController(TaskList list) {
        _taskList = list;
        _children = new HashMap<UUID, TaskController>();
        for (Task task : list.getTasks()) {
            TaskController controller = new TaskController(task, this);
            _children.put(task.getGUID(), controller);
        }
    }

    public Observable addTask(CharSequence description) {
        Task task = new Task(_taskList, description.toString());
        TaskController controller = new TaskController(task, this);
        _children.put(task.getGUID(), controller);
        _taskList.addTask(task);
        return task;
    }

    public TaskController getChild(UUID guid) {
        return _children.get(guid);
    }
}
