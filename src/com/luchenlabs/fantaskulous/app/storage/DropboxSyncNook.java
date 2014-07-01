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
        public void onFileChange(DbxFile file) {
            try {
                if (file.getSyncStatus().isLatest) {
                    _mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text = "New file downloaded, but not doing anything with it";
                            Toast.makeText(_mainActivity,
                                    text, Toast.LENGTH_SHORT).show();
                            ;
                        }
                    });
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
    private DbxFile _file;
    private final Listener _listener = new FileDownloadedListener();

    public DropboxSyncNook(Activity mainActivity, String filename) {
        _mainActivity = mainActivity;
        this._filename = filename;
    }

    @Override
    public void cleanup() {
        if (_file != null) {
            _file.removeListener(_listener);
            _file.close();
        }
    }

    @Override
    public InputStream fetchMeAnInputStream() {

        if (_file == null)
            _file = findDbxFile();

        if (_file == null)
            return null;

        try {
            return _file.getReadStream();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IoEx: " + e.getMessage()); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        _file = findDbxFile();
        if (_file == null)
            return null;

        try {
            return _file.getWriteStream();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IoEx: " + e.getMessage()); //$NON-NLS-1$
        }

        return null;
    }

    private DbxFile findDbxFile() {
        _dbxAcctMgr = DbxAccountManager.getInstance(_mainActivity.getApplicationContext(),
                C.DBX_APP_KEY,
                C.DBX_APP_SECRET);
        if (!_dbxAcctMgr.hasLinkedAccount()) {
            Log.i(getClass().getSimpleName(), "No dropbox account is linked");
            return null;
        }

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

//            file.addListener(_listener);
            DbxFileStatus syncStatus = file.getSyncStatus();
            if (!syncStatus.isLatest) {
                if (file.getNewerStatus().bytesTransferred > 0) {
                    // Currently downloading
                    _mainActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String text = "Dropbox file is stale, waiting for new version";
                            Toast.makeText(_mainActivity, text, Toast.LENGTH_SHORT).show();
                            ;
                        }

                    });
                    return null;
                }
            }

        } catch (DbxException e) {
            Log.e(getClass().getSimpleName(), "DbxEx: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }

        return file;
    }
}