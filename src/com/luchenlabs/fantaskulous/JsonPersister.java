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
import com.luchenlabs.fantaskulous.model.TaskLists;

public class JsonPersister implements IPersister {

    public static String getJSON(TaskLists object) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String json = gson.toJson(object);
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.luchenlabs.fantaskulous.IPersister#load(java.io.InputStream)
     */
    @Override
    public List<TaskList> load(InputStream is) {
        InputStreamReader reader = new InputStreamReader(is);
        Gson gson = new Gson();
        TaskLists lol = gson.fromJson(reader, TaskLists.class);
        if (lol == null)
            return null;
        return lol.lists;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.luchenlabs.fantaskulous.IPersister#save(java.io.OutputStream,
     * java.util.ArrayList)
     */
    @Override
    public void save(OutputStream os, List<TaskList> lists) throws IOException {
        TaskLists object = new TaskLists();
        object.lists = new ArrayList<TaskList>(lists);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        String json = getJSON(object);
        writer.write(json);
        writer.close();
    }
}
