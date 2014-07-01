package com.luchenlabs.fantaskulous.app.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.app.MainActivity;

public class FallbackAssetCranny implements NookOrCranny {

    /**
     * 
     */
    private final MainActivity _mainActivity;
    private final String _filename;

    public FallbackAssetCranny(MainActivity mainActivity, String filename) {
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
            AssetManager assetManager = _mainActivity.getAssets();
            return assetManager.open(_filename);
        } catch (IOException e) {
            Log.wtf(getClass().getSimpleName(), _mainActivity.getString(R.string.fmt_not_found, _filename, e));
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        throw new UnsupportedOperationException("Not implemented!"); //$NON-NLS-1$
    }

}