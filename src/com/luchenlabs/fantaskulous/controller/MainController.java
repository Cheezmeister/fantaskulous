package com.luchenlabs.fantaskulous.controller;

import java.util.List;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

@SuppressWarnings("static-method")
public class MainController {
    public TaskList createTaskList(List<TaskList> lists, CharSequence title) {
        TaskList list = new TaskList(title.toString());
        lists.add(list);
        return list;
    }

    public boolean moveTaskToList(Task task, TaskList oldList, TaskList newList) {
        boolean modified = oldList.removeTask(task);
        newList.addTask(task);
        return modified;
    }

    public boolean moveTaskToNextList(Task task, TaskList currentList, List<TaskList> lists) {
        int newListIndex = lists.indexOf(currentList) + 1;
        newListIndex %= lists.size();
        TaskList newList = lists.get(newListIndex);
        return moveTaskToList(task, currentList, newList);
    }

    public boolean removeTaskList(List<TaskList> lists, CharSequence name) {
        for (TaskList task : lists) {
            if (task.getName().equals(name))
                return lists.remove(task);
        }
        return false;
    }
}
