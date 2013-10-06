package com.luchenlabs.fantaskulous;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.luchenlabs.fantaskulous.model.TaskList;

public class Persister {

    private static class TaskLists {
        public ArrayList<TaskList> lists = new ArrayList<TaskList>();
    }

    public static List<TaskList> load(InputStream is) {
        InputStreamReader reader = new InputStreamReader(is);
        Gson gson = new Gson();
        return gson.fromJson(reader, TaskLists.class).lists;
    }
}
