package com.luchenlabs.fkls;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luchenlabs.fkls.core.C;
import com.luchenlabs.fkls.model.FantaskulousModel;
import com.luchenlabs.fkls.model.ListOLists;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;

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
    public FantaskulousModel load(InputStream is) {
        InputStreamReader reader = new InputStreamReader(is);
        Gson gson = new Gson();
        ListOLists lol = gson.fromJson(reader, ListOLists.class);
        if (lol == null)
            return null;
        FantaskulousModel model = new FantaskulousModel();
        model.taskLists = lol.lists;
        model.tasks = new HashMap<UUID, Task>();
        for (TaskList l : model.taskLists) {
            for (Task t : l.getTasks()) {
                model.tasks.put(t.getGUID(), t);
            }
        }
        return model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.luchenlabs.fantaskulous.IPersister#save(java.io.OutputStream,
     * java.util.ArrayList)
     */
    @Override
    public void save(OutputStream os, FantaskulousModel model) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(getJSON(model.taskLists));
        writer.close();
    }
}
