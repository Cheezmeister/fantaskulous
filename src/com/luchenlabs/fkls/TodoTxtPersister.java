package com.luchenlabs.fkls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.luchenlabs.fkls.core.C;
import com.luchenlabs.fkls.model.FantaskulousModel;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.util.U;

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
