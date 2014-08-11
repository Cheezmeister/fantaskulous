package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

@SuppressWarnings("static-method")
public class TaskListController {

    public Task addTask(FantaskulousModel model, TaskList taskList, CharSequence description) {
        Task task = new Task(taskList, description.toString());
        model.tasks.put(task.getGUID(), task);
        taskList.addTask(task);
        return task;
    }

}
