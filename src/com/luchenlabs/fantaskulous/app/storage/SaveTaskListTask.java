package com.luchenlabs.fantaskulous.app.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.luchenlabs.fantaskulous.DumbCallback;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.TaskList;

public class SaveTaskListTask extends FklsStorageTask<List<TaskList>, Boolean> {

    public SaveTaskListTask(Context context, DumbCallback<Boolean> callback) {
        super(context, callback);
    }

    @Override
    protected Boolean doInBackground(List<TaskList>... params) {
        for (IPersister persister : _persisters) {
            String filename = persister.getDefaultFilename();
            OutputStream os = null;

            NookOrCranny[] nooksAndCrannies = new NookOrCranny[] {
                    new DropboxCoreNook(_context, filename)
                    //                new LocalFileCranny(filename),
            };

            for (NookOrCranny noc : nooksAndCrannies) {

                os = noc.fetchMeAnOutputStream();

                if (os == null) continue;

                try {
                    persister.save(os, G.getState().getModel());
                    os.close();
                    noc.cleanup();
                    return true;
                } catch (IOException e) {
                    Log.e(getClass().getSimpleName(), _context.getString(R.string.fmt_access_denied, filename, e));
                } catch (Exception e) {
                    Log.wtf(getClass().getSimpleName(), String.format("Unexpected exception %s", e.toString())); //$NON-NLS-1$
                }

            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (_callback != null) _callback.call(result);
    }

}