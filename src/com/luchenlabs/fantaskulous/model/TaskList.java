package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.core.C;

public class TaskList extends Observable implements Observer, Comparable<TaskList> {

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

    @Override
    public int compareTo(TaskList another) {
        return this.name.compareTo(another.name);
    }

    private void defaults() {
        setName(C.EMPTY);
        setTasks(new ArrayList<Task>());
        setGuid(UUID.randomUUID());
    }

    public UUID getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean removeCompletedTasks() {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            if (task.isComplete()) {
                it.remove();
                setChanged();
                task.deleteObserver(this);
            }
        }
        if (this.hasChanged()) {
            notifyObservers();
            return true;
        }
        return false;
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
}
