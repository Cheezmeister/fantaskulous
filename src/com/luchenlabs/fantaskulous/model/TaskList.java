package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;
import java.util.Observable;

import com.luchenlabs.fantaskulous.C;

public class TaskList extends Observable {
    private String name;
    private ArrayList<Task> tasks;

    public TaskList() {
        defaults();
    }

    public TaskList(String name) {
        defaults();
        this.setName(name);
    }

    private void defaults() {
        setName(C.EMPTY);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setName(String name) {
        this.name = name;
        notifyObservers();
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
        notifyObservers();
    }
}