package com.luchenlabs.fkls.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * This is a silly, silly class. I got tired of typing out all those wicketies.
 * I succumbed to my weakness.
 * 
 * @author cheezmeister
 */
@Deprecated
public class ListOLists {
    @Expose
    public List<TaskList> lists = new ArrayList<TaskList>();
}