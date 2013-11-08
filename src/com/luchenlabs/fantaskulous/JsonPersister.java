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

public class JsonPersister {

    public static List<TaskList> load(InputStream is) {
        InputStreamReader reader = new InputStreamReader(is);
        Gson gson = new Gson();
        TaskLists lol = gson.fromJson(reader, TaskLists.class);
        if (lol == null)
            return null;
        return lol.lists;
    }

    public static void save(OutputStream os, ArrayList<TaskList> lists) throws IOException {
        TaskLists object = new TaskLists();
        object.lists = lists;
        OutputStreamWriter writer = new OutputStreamWriter(os);
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
        writer.write(gson.toJson(object));
        writer.close();
    }

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
}
