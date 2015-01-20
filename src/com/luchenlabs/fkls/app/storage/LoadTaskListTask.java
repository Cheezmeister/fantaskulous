package com.luchenlabs.fkls.app.storage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.luchenlabs.fkls.DumbCallback;
import com.luchenlabs.fkls.IPersister;
import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.app.storage.LoadTaskListTask.LoadResult;
import com.luchenlabs.fkls.model.FklsModel;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;
import com.luchenlabs.fkls.util.U;

public class LoadTaskListTask extends FklsStorageTask<Void, LoadResult> {

    public class LoadResult {
        public FklsModel model;
        public NookOrCranny nookOrCranny;

    }

    public LoadTaskListTask(
            Context context,
            DumbCallback<LoadResult> callback) {

        super(context, callback);
    }

    @Override
    protected LoadResult doInBackground(Void... params) {
        InputStream is = null;

        FklsModel model = null;
        LoadResult result = new LoadResult();
        for (IPersister persister : _persisters) {
            String filename = persister.getDefaultFilename();
            Log.w(getClass().getSimpleName(), "Looking for " + filename); //$NON-NLS-1$

            // Attempt load first from local, then from assets
            List<NookOrCranny> nocs = new ArrayList<NookOrCranny>();
            NookOrCranny[] _nooksAndCrannies = getNooksAndCrannies(filename);
            nocs.addAll(Arrays.asList(_nooksAndCrannies));
            nocs.add(new FallbackAssetCranny(_context, filename));

            for (NookOrCranny noc : nocs) {

                // Open if we can. If something went wrong, bail and fall
                // back
                Log.d(getClass().getSimpleName(), "Trying " + noc); //$NON-NLS-1$
                is = noc.fetchMeAnInputStream();
                if (is == null) continue;
                Log.i(getClass().getSimpleName(), "Found a stream with " + noc); //$NON-NLS-1$

                try {
                    Log.d(getClass().getSimpleName(), "Attempting to load " + persister.getDefaultFilename()); //$NON-NLS-1$
                    model = persister.load(is);
                    Log.d(getClass().getSimpleName(),
                            String.format("Loaded: %s (%d tasks)", model.toString(), model.tasks.size())); //$NON-NLS-1$
                    is.close();
                } catch (JsonParseException e) {
                    Log.e(getClass().getSimpleName(), U.Android.ex(_context, e, R.string.fmt_invalid_json, filename));
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), U.Android.ex(_context, e, R.string.fmt_invalid_json, filename));
                }
                if (model != null) {
                    Log.d(getClass().getSimpleName(), "Success!"); //$NON-NLS-1$
                    result.model = model;
                    result.nookOrCranny = noc;
                    return result;
                }

                Log.w(getClass().getSimpleName(), "Error reading " + filename); //$NON-NLS-1$
            }
        }
        Log.wtf(getClass().getSimpleName(), "Couldn't load a blasted thing!"); //$NON-NLS-1$
        model = new FklsModel();
        model.taskLists = new ArrayList<TaskList>();
        model.tasks = new HashMap<UUID, Task>();
        result.model = model;
        result.nookOrCranny = null;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(LoadResult result) {
        super.onPostExecute(result);

        _callback.call(result);
    }

}