package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskController {

    private final Task _task;

    public TaskController(Task item) {
        _task = item;
    }

    public void changeDescription(String description) {
        _task.setDescription(description == null ? C.EMPTY : description);
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
