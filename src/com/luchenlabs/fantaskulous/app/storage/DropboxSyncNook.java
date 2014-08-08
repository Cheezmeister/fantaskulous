package com.luchenlabs.fantaskulous.app.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFile.Listener;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.luchenlabs.fantaskulous.core.C;

public class DropboxSyncNook implements NookOrCranny {

    private final class FileDownloadedListener implements DbxFile.Listener {
        @Override
        public void onFileChange(final DbxFile file) {
            try {
                DbxFileStatus syncStatus = file.getSyncStatus();
                if (syncStatus.isLatest) {
                    sendUpdate(file);
                    file.removeListener(this);
                }
            } catch (DbxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private final Activity _mainActivity;

    private DbxAccountManager _dbxAcctMgr;
    private final String _filename;
    // FIXME json and todo.txt nooks will fight over this
    private static DbxFile _file;
    private final Listener _listener = new FileDownloadedListener();

    public DropboxSyncNook(Activity mainActivity, String filename) {
        _mainActivity = mainActivity;
        this._filename = filename;
    }

    @Override
    public void begAndPleadToBloodyUpdateTheDamnFile() {
        try {
            if (_file == null) {
                _file = findDbxFile(false);
            }
            if (_file == null)
                return;

            if (_file.update()) {
                sendUpdate(_file);
            } else if (!_file.getSyncStatus().isLatest) {
                final String text;
                if (_file.getNewerStatus().bytesTransferred > 0) {
                    // Currently downloading
                    text = "Dropbox file is stale, waiting for new version";
                } else {
                    text = "Newer version, but no download in progress, grr.";
                }
                _mainActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(_mainActivity, text, Toast.LENGTH_SHORT).show();
                    }

                });
                _file.addListener(_listener);
            }
        } catch (DbxException e) {
            // TODO 
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

    }

    @Override
    public void cleanup() {
        if (_file != null) {
            _file.removeListener(_listener);
            _file.close();
            _file = null;
        }
    }

    @Override
    public InputStream fetchMeAnInputStream() {

        if (_file == null)
            _file = findDbxFile(false);

        if (_file == null)
            return null;
        try {
            _file.update();
        } catch (DbxException e1) {
            // We tried
            Log.wtf(getClass().getSimpleName(), e1.getMessage());
        }

        try {
            return _file.getReadStream();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IoEx: " + e.getMessage()); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        if (_file == null) {
            Log.wtf("Finding file", "Finding file");
            _file = findDbxFile(true);
        }

        if (_file == null)
            return null;

        try {
            _file.update();
        } catch (DbxException e1) {
            // We tried
        }

        try {
            return _file.getWriteStream();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IoEx: " + e.getMessage()); //$NON-NLS-1$
        }

        return null;
    }

    private DbxFile findDbxFile(boolean create) {
        _dbxAcctMgr = DbxAccountManager.getInstance(_mainActivity.getApplicationContext(),
                C.DBX_APP_KEY,
                C.DBX_APP_SECRET);
        if (!_dbxAcctMgr.hasLinkedAccount()) {
            Log.i(getClass().getSimpleName(), "No dropbox account is linked");
            return null;
        }

        // TODO don't directly talk to prefs
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_mainActivity
                .getApplicationContext());
        if (!settings.getBoolean("prefEnableDropboxSync", false))
            return null;

        DbxFileSystem dbxFs;
        DbxFile file;
        try {
            try {
                dbxFs = DbxFileSystem.forAccount(_dbxAcctMgr.getLinkedAccount());
            } catch (Unauthorized e) {
                Log.e(getClass().getSimpleName(), "Unauthorized: " + e.getMessage()); //$NON-NLS-1$
                return null;
            }
            DbxPath path = new DbxPath(_filename);
            if (dbxFs.exists(path)) {
                file = dbxFs.open(path);
            } else {
                try {
                    file = dbxFs.create(path);
                } catch (InvalidPathException e) {
                    Log.e(getClass().getSimpleName(), "InvalidPath: " + e.getMessage()); //$NON-NLS-1$
                    return null;
                }
            }

        } catch (DbxException e) {
            Log.e(getClass().getSimpleName(), "DbxEx: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }

        return file;
    }

    private void sendUpdate(final DbxFile file) {
        _mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO reload and handleNewDataLoaded
                String text = "New file downloaded, but not doing anything with it: " + file.toString();
                Toast.makeText(_mainActivity,
                        text, Toast.LENGTH_SHORT).show();
                ;
            }
        });
    }
}