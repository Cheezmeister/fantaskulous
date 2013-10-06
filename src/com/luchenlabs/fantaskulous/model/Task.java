package com.luchenlabs.fantaskulous.model;

import java.util.Observable;
import java.util.UUID;

import org.joda.time.DateTime;

import com.luchenlabs.fantaskulous.C;

public class Task extends Observable {

    private Priority priority;
    private String description;
    private CharSequence notes;
    private DateTime date;

    public UUID guid;

    // public List<Task> blockingTasks; // TODO

    public transient TaskList parent;

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
        notifyObservers();
    }

    public void setDescription(String description) {
        this.description = description;
        notifyObservers();
    }

    public void setNotes(CharSequence notes) {
        this.notes = notes;
        notifyObservers();
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        notifyObservers();
    }

}
