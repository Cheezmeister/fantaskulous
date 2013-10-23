package com.luchenlabs.fantaskulous.model;

import java.util.Observable;
import java.util.UUID;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.luchenlabs.fantaskulous.C;

public class Task extends Observable {

    @Expose
    private Priority priority;
    @Expose
    private String description;
    @Expose
    private CharSequence notes;
    @Expose
    private boolean completed;

    // TODO @Expose private DateTime date;
    // TODO @Expose private DateTime dateOfCompletion;
    // public transient List<UUID> blockingTasks; // TODO

    @Expose
    private UUID guid;

    private transient TaskList parent;

    public Task() {
        defaults();
    }

    public Task(TaskList parent, String description) {
        defaults();
        this.parent = parent;
        this.setDescription(description);
    }

    // public DateTime getDate() {
    // return date;
    // }

    public String getDescription() {
        return description;
    }

    public CharSequence getNotes() {
        return notes;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isComplete() {
        return completed;
    }

    public void setComplete(boolean isComplete) {
        this.completed = isComplete;
        // this.dateOfCompletion = isComplete ? DateTime.now() : null;
        setChanged();
        notifyObservers();
    }

    public void setDate(DateTime date) {
        // this.date = date;
        setChanged();
        notifyObservers();
    }

    public void setDescription(String description) {
        this.description = description;
        setChanged();
        notifyObservers();
    }

    public void setNotes(CharSequence notes) {
        this.notes = notes;
        setChanged();
        notifyObservers();
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        setChanged();
        notifyObservers();
    }

    private void defaults() {
        setDescription(C.EMPTY);
        setNotes(null);
        guid = UUID.randomUUID();
        setDate(DateTime.now());
        setPriority(Priority.MEDIUM);
    }

}
