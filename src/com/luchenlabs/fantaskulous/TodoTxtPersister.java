package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;

public class TodoTxtPersister implements IPersister {

    @Override
    public String getDefaultFilename() {
        return C.TODO_TXT;
    }

    @Override
    public FantaskulousModel load(InputStream is) {
        return U.Todo.modelFromTodoTxt(is);
    }

    @Override
    public void save(OutputStream os, FantaskulousModel model) throws IOException {
        PrintWriter pw = new PrintWriter(os);
        for (Task t : model.tasks.values()) {
            pw.append(U.Todo.toTodoTxt(t)).append('\n');
        }
        pw.flush();

    }

}
