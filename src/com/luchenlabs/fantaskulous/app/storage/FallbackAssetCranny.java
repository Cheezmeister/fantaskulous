package com.luchenlabs.fantaskulous.app.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.luchenlabs.fantaskulous.R;

public class FallbackAssetCranny implements NookOrCranny {

    /**
     *
     */
    private final Context _context;
    private final String _filename;

    public FallbackAssetCranny(Context context, String filename) {
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
            AssetManager assetManager = _context.getAssets();
            return assetManager.open(_filename);
        } catch (IOException e) {
            Log.wtf(getClass().getSimpleName(), _context.getString(R.string.fmt_not_found, _filename, e));
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        throw new UnsupportedOperationException("Not implemented!"); //$NON-NLS-1$
    }

}