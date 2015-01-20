package com.luchenlabs.fkls.controller;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.core.C;
import com.luchenlabs.fkls.model.Priority;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;

@SuppressWarnings("static-method")
public class TaskController {

    public void changeDescription(Task task, String description) {
        task.setDescription(description == null ? C.EMPTY : description);
    }

    public void complete(Task task, boolean isChecked) {
        task.setComplete(isChecked);
        reSort(task);
    }

    public void cyclePriority(Task task) {
        Priority priority = task.getPriority();

        Priority newPri = priority == Priority.MEDIUM ? Priority.LOW :
                priority == Priority.LOW ? Priority.HIGH :
                        Priority.MEDIUM;

        task.setPriority(newPri);

        reSort(task);
    }

    private void reSort(Task task) {
        for (TaskList list : task.getContexts()) {
            G.getState().getTaskListController().sortList(list); // TODO HACK
        }
    }
}
