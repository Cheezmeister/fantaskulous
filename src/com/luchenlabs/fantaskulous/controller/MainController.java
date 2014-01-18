package com.luchenlabs.fantaskulous.controller;

import java.util.List;

import com.luchenlabs.fantaskulous.model.TaskList;

public class MainController {
    public TaskList createTaskList(List<TaskList> lists, CharSequence title) {
        TaskList list = new TaskList(title.toString());
        lists.add(list);
        return list;
    }

    public boolean removeTaskList(List<TaskList> lists, CharSequence name) {
        for (TaskList task : lists) {
            if (task.getName().equals(name))
                return lists.remove(task);
        }
        return false;
    }
}
