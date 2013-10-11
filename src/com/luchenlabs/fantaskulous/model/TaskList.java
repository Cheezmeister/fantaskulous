package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;
import java.util.Observable;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.C;

public class TaskList extends Observable {

    @Expose
    private String name;
    @Expose
    private ArrayList<Task> tasks;

    public TaskList() {
        defaults();
    }

    public TaskList(String name) {
        defaults();
        this.setName(name);
    }

    public void addTask(Task task) {
        tasks.add(task);
        setChanged();
        notifyObservers();
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
        setChanged();
        notifyObservers();
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
        setChanged();
        notifyObservers();
    }
}