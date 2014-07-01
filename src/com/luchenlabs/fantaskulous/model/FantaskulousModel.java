/**
 * 
 */
package com.luchenlabs.fantaskulous.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Top-level model for the app
 * 
 * @author cheezmeister
 */
public class FantaskulousModel {
    public Map<UUID, Task> tasks = new HashMap<UUID, Task>();
    // TODO public List<Task> tasks; /* ORDER MATTERS! */
    public List<TaskList> taskLists = new ArrayList<TaskList>();
}
