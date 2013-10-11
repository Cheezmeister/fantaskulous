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
    private DateTime date;

    @Expose
    private UUID guid;

    // public List<Task> blockingTasks; // TODO

    private transient TaskList parent;

    public Task() {
        defaults();
    }

    public Task(TaskList parent, String description) {
        defaults();
        this.parent = parent;
        this.setDescription(description);
    }

    private void defaults() {
        setDescription(C.EMPTY);
        setNotes(null);
        guid = UUID.randomUUID();
        setDate(DateTime.now());
        setPriority(Priority.MEDIUM);
    }

    public DateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public CharSequence getNotes() {
        return notes;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setDate(DateTime date) {
        this.date = date;
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

}
