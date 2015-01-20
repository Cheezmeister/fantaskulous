package com.luchenlabs.fkls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.luchenlabs.fkls.model.FantaskulousModel;

public interface IPersister {

    public abstract String getDefaultFilename();

    public abstract FantaskulousModel load(InputStream is);

    public abstract void save(OutputStream os, FantaskulousModel model) throws IOException;

}