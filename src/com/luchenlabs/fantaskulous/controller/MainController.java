package com.luchenlabs.fantaskulous.controller;

import java.util.List;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.model.SigillyTaskList;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskContext;
import com.luchenlabs.fantaskulous.model.TaskList;

@SuppressWarnings("static-method")
public class MainController {
    public TaskList createTaskList(List<TaskList> lists, CharSequence title) {
        SigillyTaskList list = new TaskContext(title.toString());
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

    public void removeAllCompletedTasks(List<TaskList> lists) {
        for (TaskList l : lists) {
            l.removeCompletedTasks();
        }
    }

    public boolean removeTaskList(List<TaskList> lists, CharSequence name) {
        for (TaskList list : lists) {
            if (list.getName().equals(name)) return lists.remove(list);
        }
        return false;
    }

    public void sortAll(List<TaskList> lists) {
        for (TaskList list : lists) {
            G.getState().getTaskListController().sortList(list);
        }
    }
}
