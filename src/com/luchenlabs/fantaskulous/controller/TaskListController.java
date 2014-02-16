package com.luchenlabs.fantaskulous.controller;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

@SuppressWarnings("static-method")
public class TaskListController {

    public Task addTask(TaskList taskList, CharSequence description) {
        Task task = new Task(taskList, description.toString());
        taskList.addTask(task);
        return task;
    }

}
