package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.ListOLists;

public class JsonPersister implements IPersister {

    public static String getJSON(List<TaskList> lists) {
        ListOLists lol = new ListOLists();
        lol.lists = lists;
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String json = gson.toJson(lol);
        return json;
    }

    @Override
    public String getDefaultFilename() {
        return C.TASK_FILE;
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
        ListOLists lol = gson.fromJson(reader, ListOLists.class);
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
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(getJSON(lists));
        writer.close();
    }
}
