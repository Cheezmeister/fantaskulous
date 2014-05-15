package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.TaskLists;

public class JsonPersister implements IPersister {

    private static class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return new DateTime(json.getAsString());
        }

        // No need for an InstanceCreator since DateTime provides a no-args
        // constructor
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static String getJSON(TaskLists object) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
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
