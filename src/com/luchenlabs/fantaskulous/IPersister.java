package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.luchenlabs.fantaskulous.model.TaskList;

public interface IPersister {

    public abstract String getDefaultFilename();

    public abstract List<TaskList> load(InputStream is);

    public abstract void save(OutputStream os, List<TaskList> lists) throws IOException;

}