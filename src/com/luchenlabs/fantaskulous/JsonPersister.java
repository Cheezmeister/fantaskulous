package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luchenlabs.fantaskulous.model.TaskList;

public class JsonPersister {

    public static List<TaskList> load(InputStream is) {
        InputStreamReader reader = new InputStreamReader(is);
        Gson gson = new Gson();
        return gson.fromJson(reader, TaskLists.class).lists;
    }

    public static void save(OutputStream os, ArrayList<TaskList> lists) throws IOException {
        TaskLists object = new TaskLists();
        object.lists = lists;
        OutputStreamWriter writer = new OutputStreamWriter(os);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        writer.write(gson.toJson(object));
    }

    private static class TaskLists {
        public ArrayList<TaskList> lists = new ArrayList<TaskList>();
    }
}
