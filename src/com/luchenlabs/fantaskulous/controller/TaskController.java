package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskController {

    private final Task _task;

    private final TaskListController _parent;

    public TaskController(Task item, TaskListController parent) {
        _task = item;
        _parent = parent;
    }

    public void complete(boolean isChecked) {
        _task.setComplete(isChecked);
    }

    public void cyclePriority() {
        Priority priority = _task.getPriority();
        int newPriority = priority.ordinal() + 1;
        newPriority %= Priority.values().length;
        _task.setPriority(Priority.values()[newPriority]);
    }
}
