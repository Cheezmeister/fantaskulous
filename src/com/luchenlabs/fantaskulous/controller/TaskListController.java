package com.luchenlabs.fantaskulous.controller;

import java.util.Observable;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListController {

    public Observable addTask(TaskList taskList, CharSequence description) {
        Task task = new Task(taskList, description.toString());
        taskList.addTask(task);
        return task;
    }

}
