package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class TaskLists {
    @Expose
    public ArrayList<TaskList> lists = new ArrayList<TaskList>();
}