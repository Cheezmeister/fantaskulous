package com.luchenlabs.fkls.controller;

import java.util.Collections;
import java.util.Comparator;

import com.luchenlabs.fkls.model.FklsModel;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;

@SuppressWarnings("static-method")
public class TaskListController {

    /**
     * Sorts tasks from high to low priority, completed tasks at bottom
     */
    private static final Comparator<Task> _sortComparator = new Comparator<Task>() {
        @Override
        public int compare(Task lhs, Task rhs) {
            int compComp = Boolean.valueOf(lhs.isComplete()).compareTo(rhs.isComplete());
            if (compComp != 0) return compComp;

            int priComp = lhs.getPriority().compareTo(rhs.getPriority());
            if (priComp != 0) return priComp;

            return 0;
        }

    };

    public Task addTask(FklsModel model, TaskList taskList, CharSequence description) {
        Task task = new Task(taskList, description.toString());
        model.tasks.put(task.getGUID(), task);
        taskList.addTask(task);
        sortList(taskList);
        return task;
    }

    public void sortList(TaskList taskList) {
        Collections.sort(taskList.getTasks(), _sortComparator);
    }

}
