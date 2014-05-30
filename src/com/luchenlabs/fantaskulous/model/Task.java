package com.luchenlabs.fantaskulous.model;

import java.util.Date;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.C;

public class Task extends Observable {

    @Expose
    private Priority priority;

    // Date fields not used by us and exist solely to avoid clobbering existing
    // todo.txt entries
    @Expose
    private Date date;

    @Expose
    private Date dateOfCompletion;

    @Expose
    private String description;

    @Expose
    private boolean completed;

    @Expose
    private UUID guid;

    private transient SortedSet<TaskProject> projects;

    private transient SortedSet<TaskContext> contexts;

    // public transient List<UUID> blockingTasks; // TODO

    public Task() {
        defaults();
    }

    public Task(TaskList parent, String description) {
        defaults();
        this.setDescription(description);
    }

    private void defaults() {
        projects = new TreeSet<TaskProject>();
        contexts = new TreeSet<TaskContext>();
        setDescription(C.EMPTY);
        guid = UUID.randomUUID();
        setDate(new Date());
        setPriority(Priority.MEDIUM);
    }

    public SortedSet<TaskContext> getContexts() {
        return this.contexts;
    }

    public String getDescription() {
        return description;
    }

    public UUID getGUID() {
        return guid;
    }

    public Priority getPriority() {
        return priority;
    }

    public SortedSet<TaskProject> getProjects() {
        return projects;
    }

    public boolean isComplete() {
        return completed;
    }

    public void setComplete(boolean isComplete) {
        this.completed = isComplete;
        this.priority = Priority.NONE;
        setChanged();
        notifyObservers();
    }

    public void setDate(Date date) {
        this.date = date;
        setChanged();
        notifyObservers();
    }

    public void setDescription(String description) {
        this.description = description;
        setChanged();
        notifyObservers();
    }

    public void setGUID(String string) {
        this.guid = UUID.fromString(string);
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        setChanged();
        notifyObservers();
    }

}
