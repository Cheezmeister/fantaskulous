package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.C;

public class TaskList extends Observable implements Observer {

    @Expose
    private String name;
    @Expose
    private ArrayList<Task> tasks;
    @Expose
    private UUID guid;

    public TaskList() {
        defaults();
    }

    public TaskList(String name) {
        defaults();
        this.setName(name);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.addObserver(this);
        setChanged();
        notifyObservers();
    }

    public UUID getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public boolean removeTask(Task task) {
        boolean modified = tasks.remove(task);
        if (modified) {
            setChanged();
            notifyObservers();
        }
        return modified;
    }

    public void setGuid(UUID guid) {
        this.guid = guid;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable observable, Object data) {
        setChanged();
        notifyObservers();
    }

    private void defaults() {
        setName(C.EMPTY);
        setTasks(new ArrayList<Task>());
    }
}