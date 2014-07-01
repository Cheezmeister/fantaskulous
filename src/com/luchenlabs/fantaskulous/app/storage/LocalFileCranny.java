package com.luchenlabs.fantaskulous.app.storage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.app.MainActivity;

public class LocalFileCranny implements NookOrCranny {

    /**
     * 
     */
    private final MainActivity _mainActivity;
    private final String _filename;

    public LocalFileCranny(MainActivity mainActivity, String filename) {
        _mainActivity = mainActivity;
        this._filename = filename;
    }

    @Override
    public void cleanup() {
        // Nothing to do
    }

    @Override
    public InputStream fetchMeAnInputStream() {
        try {
            return _mainActivity.openFileInput(_filename);
        } catch (FileNotFoundException e) {
            Log.w(getClass().getSimpleName(), _mainActivity.getString(R.string.fmt_not_found, _filename, e));
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        try {
            return _mainActivity.openFileOutput(_filename, MainActivity.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getSimpleName(), _mainActivity.getString(R.string.fmt_access_denied, _filename, e));
        }
        return null;
    }
}