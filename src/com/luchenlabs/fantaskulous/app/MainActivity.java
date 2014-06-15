package com.luchenlabs.fantaskulous.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.NookOrCranny;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.TodoTxtPersister;
import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;

public class MainActivity extends AbstractActivity {

    public class DropboxSyncNook implements NookOrCranny {

        private DbxAccountManager _dbxAcctMgr;
        private final String _filename;
        private DbxFile _file;

        public DropboxSyncNook(String filename) {
            this._filename = filename;
        }

        @Override
        public void cleanup() {
            if (_file != null) {
                _file.close();
            }
        }

        @Override
        public InputStream fetchMeAnInputStream() {
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
            _dbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                    C.DBX_APP_KEY,
                    C.DBX_APP_SECRET);
            if (!_dbxAcctMgr.hasLinkedAccount()) {
                Log.i(getClass().getSimpleName(), "No dropbox account is linked");
                return null;
            }

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
    }

    public class FallbackAssetCranny implements NookOrCranny {

        private final String _filename;

        public FallbackAssetCranny(String filename) {
            this._filename = filename;
        }

        @Override
        public void cleanup() {
            // Nothing to do
        }

        @Override
        public InputStream fetchMeAnInputStream() {
            try {
                AssetManager assetManager = getAssets();
                return assetManager.open(_filename);
            } catch (IOException e) {
                Log.wtf(getClass().getSimpleName(), getString(R.string.fmt_not_found, _filename, e));
            }
            return null;
        }

        @Override
        public OutputStream fetchMeAnOutputStream() {
            throw new UnsupportedOperationException("Not implemented!"); //$NON-NLS-1$
        }

    }

    private class LoadTaskListTask extends AsyncTask<Void, Void, FantaskulousModel> {

        private FantaskulousModel attemptLoad(IPersister persister, InputStream is) throws JsonParseException,
                Exception {
            if (is != null) { return (persister.load(is)); }
            return null;
        }

        @Override
        protected FantaskulousModel doInBackground(Void... params) {
            InputStream is = null;

            FantaskulousModel model = null;
            for (IPersister persister : persisters) {
                String filename = persister.getDefaultFilename();
                Log.i(getClass().getSimpleName(), "Looking for " + filename); //$NON-NLS-1$

                // Attempt load first from local, then from assets
                List<NookOrCranny> nocs = new ArrayList<NookOrCranny>();
                nocs.addAll(Arrays.asList(defaultNooksAndCrannies(filename)));
                nocs.add(new FallbackAssetCranny(filename));

                for (NookOrCranny noc : nocs) {

                    // Open if we can. If something went wrong, bail and fall
                    // back
                    is = noc.fetchMeAnInputStream();
                    if (is == null)
                        continue;
                    Log.i(getClass().getSimpleName(), "Found a stream"); //$NON-NLS-1$

                    try {
                        model = attemptLoad(persister, is);
                        is.close();
                        noc.cleanup();
                    } catch (JsonParseException e) {
                        Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                    }
                    if (model != null) {
                        Log.d(getClass().getSimpleName(), "Success!"); //$NON-NLS-1$
                        return model;
                    }

                    Log.d(getClass().getSimpleName(), "Error reading " + filename); //$NON-NLS-1$
                }
            }
            model = new FantaskulousModel();
            model.taskLists = new ArrayList<TaskList>();
            model.tasks = new HashMap<UUID, Task>();
            return model;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(FantaskulousModel result) {
            super.onPostExecute(result);

            handleFinishedLoading(result);
        }

    }

    public class LocalFileCranny implements NookOrCranny {

        private final String _filename;

        public LocalFileCranny(String filename) {
            this._filename = filename;
        }

        @Override
        public void cleanup() {
            // Nothing to do
        }

        @Override
        public InputStream fetchMeAnInputStream() {
            try {
                return openFileInput(_filename);
            } catch (FileNotFoundException e) {
                Log.w(getClass().getSimpleName(), getString(R.string.fmt_not_found, _filename, e));
            }
            return null;
        }

        @Override
        public OutputStream fetchMeAnOutputStream() {
            try {
                return openFileOutput(_filename, MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, _filename, e));
            }
            return null;
        }
    }

    private class SaveTaskListTask extends AsyncTask<List<TaskList>, Void, Void> {

        @Override
        protected Void doInBackground(List<TaskList>... params) {
            for (IPersister persister : persisters) {
                String filename = persister.getDefaultFilename();
                OutputStream os = null;

                NookOrCranny[] nooksAndCrannies = defaultNooksAndCrannies(filename);

//                new NookOrCranny[] {
//                        new LocalFileCranny(filename)
//                };

                for (NookOrCranny noc : nooksAndCrannies) {

                    os = noc.fetchMeAnOutputStream();

                    if (os == null)
                        continue;

                    try {
                        persister.save(os, G.getState().getModel());
                        os.close();
                        noc.cleanup();
                    } catch (IOException e) {
                        Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, filename, e));
                    } catch (Exception e) {
                        Log.wtf(getClass().getSimpleName(), String.format("Unexpected exception %s", e.toString())); //$NON-NLS-1$
                    }

                }
            }
            return null;
        }

    }

    private static final int CODE_IMPORT = 3456;
    private static final int CODE_DBX_LINK_ACCOUNT = 7890;
    private static final int CODE_DBX_CHOOSER = 8484;

    private TaskListFragmentPagerAdapter _pagerAdapter;

    private ViewPager _viewPager;

    private View _spinner;

    private MainController _controller;

    private final IPersister[] persisters = {
            new TodoTxtPersister(),
            new JsonPersister(),
    };

    private void createList() {
        showTextInputDialog(
                getString(R.string.new_list),
                getString(R.string.list_name),
                new StringRunnable() {
                    @Override
                    public void run(String string) {
                        List<TaskList> taskLists = G.getState().getModel().taskLists;
                        TaskList list = _controller.createTaskList(taskLists, string);
                        if (list != null) {
                            _pagerAdapter.presentNewList(list);
                            _viewPager.refreshDrawableState();
                        }
                    }
                });
    }

    private NookOrCranny[] defaultNooksAndCrannies(String filename) {
        return new NookOrCranny[] {
                new DropboxSyncNook(filename),
        //                new LocalFileCranny(filename),
        };
    }

    private void export() {
        Intent send = new Intent();
        send.setAction(Intent.ACTION_SEND);
        FantaskulousModel model = G.getState().getModel();
        if (model == null) {
            Log.wtf(getClass().getSimpleName(), "No model found"); //$NON-NLS-1$
        }
        send.putExtra(Intent.EXTRA_TEXT, JsonPersister.getJSON((model.taskLists)));
        send.setType("text/json"); //$NON-NLS-1$
        startActivity(Intent.createChooser(send, getString(R.string.export_tasks)));
    }

    private void finishOnCreate() {
        _spinner.setVisibility(View.GONE);
        _controller = G.getState().getMainController();
        refresh();
    }

    private void handleFinishedLoading(FantaskulousModel model) {
        G.getState().setModel(model);
        finishOnCreate();
    }

    private void importFuckJavaKeywords() {
        String title = getString(R.string.import_tasks);
        Intent get = new Intent(Intent.ACTION_GET_CONTENT);
        get.setType("text/json"); //$NON-NLS-1$
        startActivityForResult(Intent.createChooser(get, title), CODE_IMPORT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == CODE_IMPORT) {
            Uri uri = data.getData();
            if (uri != null) {
                ContentResolver cr = getContentResolver();
                try {
                    InputStream is = cr.openInputStream(uri);
                    IPersister persister = new JsonPersister();
                    // TODO import todo.txt
                    this.handleFinishedLoading(persister.load(is));
                } catch (IOException e) {
                    Log.e(getClass().getSimpleName(), "Importing failed", e); //$NON-NLS-1$
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _spinner = findViewById(R.id.spinner);
        _viewPager = (ViewPager) findViewById(R.id.pager);

        _pagerAdapter = new TaskListFragmentPagerAdapter(
                getSupportFragmentManager());

        _viewPager.setAdapter(_pagerAdapter);

        FantaskulousModel model = G.getState().getModel();
        if (model == null) {
            _spinner.setVisibility(View.VISIBLE);
            Log.i(getClass().getSimpleName(), "Kicking off load task"); //$NON-NLS-1$
            new LoadTaskListTask().execute();
        } else {
            Log.i(getClass().getSimpleName(), "Finishing on create with " + model.taskLists.size() + "lists"); //$NON-NLS-1$ //$NON-NLS-2$
            finishOnCreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onMenuItemSelected(int,
     * android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.action_create:
                createList();
                break;
            case R.id.action_remove:
                removeList();
                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_backup:
                export();
                break;
            case R.id.action_import:
                importFuckJavaKeywords();
                break;
            case R.id.action_cleanup:
                _controller.removeAllCompletedTasks(G.getState().getModel().taskLists);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        saveTasks();
        super.onStop();
    }

    private void refresh() {
        _viewPager.invalidate();
        _pagerAdapter.refresh();
    }

    private void removeList() {
        int position = _viewPager.getCurrentItem();
        CharSequence name = _pagerAdapter.getPageTitle(position);
        _controller.removeTaskList(G.getState().getModel().taskLists, name);
        refresh();
        _viewPager.setAdapter(_pagerAdapter);
    }

    @SuppressWarnings("unchecked")
    private void saveTasks() {
        FantaskulousModel model = G.getState().getModel();
        if (model != null) {
            new SaveTaskListTask().execute((model.taskLists));
        }
    }

}
