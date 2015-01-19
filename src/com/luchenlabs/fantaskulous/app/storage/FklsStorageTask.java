package com.luchenlabs.fantaskulous.app.storage;

import android.content.Context;
import android.os.AsyncTask;

import com.luchenlabs.fantaskulous.DumbCallback;
import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.TodoTxtPersister;

public abstract class FklsStorageTask<Params, Result> extends AsyncTask<Params, Void, Result> {

    protected final Context _context;
    protected final IPersister[] _persisters = {
            new TodoTxtPersister(),
            new JsonPersister(),
    };

    protected final DumbCallback<Result> _callback;

    public FklsStorageTask(Context context, DumbCallback<Result> callback) {
        _context = context;
        _callback = callback;
    }

    public FklsStorageTask(Context context, IPersister[] persisters, DumbCallback<Result> callback) {
        this(context, callback);
    }

    protected NookOrCranny[] getNooksAndCrannies(String filename) {
        NookOrCranny[] _nooksAndCrannies = new NookOrCranny[] {
                new DropboxCoreNook(_context, filename),
                new LocalFileCranny(_context, filename)
        };
        return _nooksAndCrannies;
    }
}