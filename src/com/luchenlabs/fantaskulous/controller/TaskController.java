package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

@SuppressWarnings("static-method")
public class TaskController {

    public void changeDescription(Task task, String description) {
        task.setDescription(description == null ? C.EMPTY : description);
    }

    public void complete(Task task, boolean isChecked) {
        task.setComplete(isChecked);
    }

    public void cyclePriority(Task task) {
        Priority priority = task.getPriority();
        int newPriority = priority.ordinal() + 1;
        newPriority %= Priority.values().length;
        task.setPriority(Priority.values()[newPriority]);
    }
}
