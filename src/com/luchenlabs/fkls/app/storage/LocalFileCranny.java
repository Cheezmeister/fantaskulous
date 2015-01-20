package com.luchenlabs.fkls.app.storage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.app.MainActivity;

public class LocalFileCranny implements NookOrCranny {

    /**
     *
     */
    private final Context _context;
    private final String _filename;

    public LocalFileCranny(Context context, String filename) {
        _context = context;
        this._filename = filename;
    }

    @Override
    public void begAndPleadToBloodyUpdateTheDamnFile() {
        // Nothing to do
    }

    @Override
    public void cleanup() {
        // Nothing to do
    }

    @Override
    public InputStream fetchMeAnInputStream() {
        try {
            return _context.openFileInput(_filename);
        } catch (FileNotFoundException e) {
            Log.w(getClass().getSimpleName(), _context.getString(R.string.fmt_not_found, _filename, e));
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        try {
            return _context.openFileOutput(_filename, MainActivity.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getSimpleName(), _context.getString(R.string.fmt_access_denied, _filename, e));
        }
        return null;
    }
}