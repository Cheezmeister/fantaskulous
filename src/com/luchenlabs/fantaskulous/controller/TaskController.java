
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
        task.notifyObservers();
    }

    public void cyclePriority(Task task) {
        Priority priority = task.getPriority();

        Priority newPri = priority == Priority.MEDIUM ? Priority.LOW :
                priority == Priority.LOW ? Priority.HIGH :
                        Priority.MEDIUM;

        task.setPriority(newPri);
    }
}
