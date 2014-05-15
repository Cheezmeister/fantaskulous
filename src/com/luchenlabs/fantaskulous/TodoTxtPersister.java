package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.luchenlabs.fantaskulous.model.TaskList;

public class TodoTxtPersister implements IPersister {

    @Override
    public List<TaskList> load(InputStream is) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(OutputStream os, List<TaskList> lists) throws IOException {
        PrintWriter pw = new PrintWriter(os);
        for (TaskList l : lists) {
            pw.append(U.toTodoTxt(l));
        }
        pw.flush();

    }

}
