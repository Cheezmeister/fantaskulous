package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;

public interface ITaskList {

    public abstract String getName();

    public abstract ArrayList<Task> getTasks();

    public abstract boolean removeTask(Task task);

    public abstract void setName(String name);

    public abstract void setTasks(ArrayList<Task> tasks);

}
