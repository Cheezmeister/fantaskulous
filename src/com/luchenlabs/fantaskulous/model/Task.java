package com.luchenlabs.fantaskulous.model;

import java.util.Date;
import java.util.Observable;
import java.util.UUID;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.C;

public class Task extends Observable {

    @Expose
    private Priority priority;

    // These fields not used by us and exist solely to avoid clobbering existing
    // todo.txt entries
    @Expose
    private Date date;
    @Expose
    private Date dateOfCompletion;

    // public transient List<UUID> blockingTasks; // TODO

    @Expose
    private String description;

    @Expose
    private boolean completed;

    @Expose
    private UUID guid;

    public Task() {
        defaults();
    }

    public Task(TaskList parent, String description) {
        defaults();
        this.setDescription(description);
    }

    private void defaults() {
        setDescription(C.EMPTY);
        guid = UUID.randomUUID();
        setDate(DateTime.now());
        setPriority(Priority.MEDIUM);
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

    public boolean isComplete() {
        return completed;
    }

    public void setComplete(boolean isComplete) {
        this.completed = isComplete;
        setChanged();
        notifyObservers();
    }

    public void setDate(DateTime date) {
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
