package com.luchenlabs.fkls.controller;

import java.util.Collection;
import java.util.List;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.model.SigillyTaskList;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskContext;
import com.luchenlabs.fkls.model.TaskList;

@SuppressWarnings("static-method")
public class MainController {
    public TaskList createTaskList(List<TaskList> lists, CharSequence title) {
        SigillyTaskList list = new TaskContext(title.toString());
        lists.add(list);
        return list;
    }

    public boolean moveTaskToList(Task task, Collection<? extends TaskList> oldLists, TaskList newList) {
        boolean modified = false;
        for (TaskList l : oldLists) {
            modified |= l.removeTask(task);
        }
        newList.addTask(task);
        task.notifyObservers();
        return modified;
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
