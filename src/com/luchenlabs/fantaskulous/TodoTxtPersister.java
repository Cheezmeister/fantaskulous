package com.luchenlabs.fantaskulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.util.U;

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
