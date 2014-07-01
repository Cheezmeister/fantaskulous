package com.luchenlabs.fantaskulous.app.storage;

import java.io.InputStream;
import java.io.OutputStream;

public interface NookOrCranny {

    void begAndPleadToBloodyUpdateTheDamnFile();

    /**
     * Signals we're finished writing and should close any open resources.
     * 
     * Must be called after {@link #fetchMeAnOutputStream()}
     */
    void cleanup();

    InputStream fetchMeAnInputStream();

    OutputStream fetchMeAnOutputStream();

}
