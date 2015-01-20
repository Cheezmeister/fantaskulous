package com.luchenlabs.fkls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.luchenlabs.fkls.model.FklsModel;

public interface IPersister {

    public abstract String getDefaultFilename();

    public abstract FklsModel load(InputStream is);

    public abstract void save(OutputStream os, FklsModel model) throws IOException;

}