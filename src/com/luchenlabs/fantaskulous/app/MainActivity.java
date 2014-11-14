package com.luchenlabs.fantaskulous.app;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.TodoTxtPersister;
import com.luchenlabs.fantaskulous.app.storage.DropboxCoreNook;
import com.luchenlabs.fantaskulous.app.storage.FallbackAssetCranny;
import com.luchenlabs.fantaskulous.app.storage.NookOrCranny;
import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;

public class MainActivity extends AbstractActivity {

    private static final int CODE_IMPORT = 3456;

    private TaskListFragmentPagerAdapter _pagerAdapter;
    private ViewPager _viewPager;

    private View _spinner;
    private MainController _controller;
    private final IPersister[] persisters = {
            new TodoTxtPersister(),
            //            new JsonPersister(),
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
                new DropboxCoreNook(this, filename)
        //                new LocalFileCranny(filename),
        };
    }

    private void export() {
        Intent send = new Intent(Intent.ACTION_SEND);
        FantaskulousModel model = G.getState().getModel();
        if (model == null) {
            Log.wtf(getClass().getSimpleName(), "No model found"); //$NON-NLS-1$
        }
        send.putExtra(Intent.EXTRA_TEXT, JsonPersister.getJSON((model.taskLists)));
        send.setType("text/json"); //$NON-NLS-1$
        startActivity(Intent.createChooser(send, getString(R.string.export_tasks)));
    }

    private void finishOnStart(FantaskulousModel model) {
        _controller = G.getState().getMainController();

        handleNewDataLoaded(model);

        _viewPager.setVisibility(View.VISIBLE);
        _spinner.setVisibility(View.GONE);
    }

    private void handleFinishedLoading(LoadTaskListTask.Result result) {
        FantaskulousModel model = result.model;
        G.getState().setDataSource(result.nookOrCranny);
        finishOnStart(model);
    }

    private void handleNewDataLoaded(FantaskulousModel model) {
        G.getState().setModel(model);
        _pagerAdapter = new TaskListFragmentPagerAdapter(
                getSupportFragmentManager());
        _viewPager.setAdapter(_pagerAdapter);
        refresh();
    }

    private void importFuckJavaKeywords() {
        String title = getString(R.string.import_tasks);
        Intent get = new Intent(Intent.ACTION_GET_CONTENT);
        get.setType("text/json"); //$NON-NLS-1$
        startActivityForResult(Intent.createChooser(get, title), CODE_IMPORT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        if (requestCode == CODE_IMPORT) {
            Uri uri = data.getData();
            if (uri != null) {
                ContentResolver cr = getContentResolver();
                try {
                    InputStream is = cr.openInputStream(uri);
                    IPersister persister = new JsonPersister();
                    // TODO import todo.txt
                    this.handleNewDataLoaded(persister.load(is));
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

    @Override
    protected void onStart() {
        super.onStart();
        FantaskulousModel model = G.getState().getModel();
        if (model == null) {
            _spinner.setVisibility(View.VISIBLE);
            Log.i(getClass().getSimpleName(), "Kicking off load task"); //$NON-NLS-1$
            new LoadTaskListTask().execute();
        } else {
            Log.i(getClass().getSimpleName(), "Finishing onStart with " + model.taskLists.size() + "lists"); //$NON-NLS-1$ //$NON-NLS-2$
            finishOnStart(model);
        }
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
        if (_controller.removeTaskList(G.getState().getModel().taskLists, name)) {
            _pagerAdapter.destroyItem(_viewPager, position);
            _viewPager.setAdapter(_pagerAdapter);
            refresh();
        }
    }

    @SuppressWarnings("unchecked")
    private void saveTasks() {
        FantaskulousModel model = G.getState().getModel();
        if (model != null) {
            new SaveTaskListTask().execute((model.taskLists));
        }
    }

    private class LoadTaskListTask extends AsyncTask<Void, Void, LoadTaskListTask.Result> {

        @Override
        protected Result doInBackground(Void... params) {
            InputStream is = null;

            FantaskulousModel model = null;
            for (IPersister persister : persisters) {
                String filename = persister.getDefaultFilename();
                Log.w(getClass().getSimpleName(), "Looking for " + filename); //$NON-NLS-1$

                // Attempt load first from local, then from assets
                List<NookOrCranny> nocs = new ArrayList<NookOrCranny>();
                nocs.addAll(Arrays.asList(defaultNooksAndCrannies(filename)));
                nocs.add(new FallbackAssetCranny(MainActivity.this, filename));

                for (NookOrCranny noc : nocs) {

                    // Open if we can. If something went wrong, bail and fall
                    // back
                    Log.w(getClass().getSimpleName(), "Trying " + noc);
                    is = noc.fetchMeAnInputStream();
                    if (is == null) continue;
                    Log.w(getClass().getSimpleName(), "Found a stream with " + noc); //$NON-NLS-1$

                    try {
                        Log.w(getClass().getSimpleName(), "Attempting to load " + persister.getDefaultFilename());
                        model = persister.load(is);
                        Log.w(getClass().getSimpleName(),
                                String.format("Loaded: %s (%d tasks)", model.toString(), model.tasks.size()));
                        is.close();
                    } catch (JsonParseException e) {
                        Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                    }
                    if (model != null) {
                        Log.w(getClass().getSimpleName(), "Success!"); //$NON-NLS-1$
                        Result result = new Result();
                        result.model = model;
                        result.nookOrCranny = noc;
                        return result;
                    }

                    Log.w(getClass().getSimpleName(), "Error reading " + filename); //$NON-NLS-1$
                }
            }
            Log.wtf(getClass().getSimpleName(), "Couldn't load a blasted thing!"); //$NON-NLS-1$
            model = new FantaskulousModel();
            model.taskLists = new ArrayList<TaskList>();
            model.tasks = new HashMap<UUID, Task>();
            Result result = new Result();
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
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            handleFinishedLoading(result);
        }

        public class Result {

            public FantaskulousModel model;
            public NookOrCranny nookOrCranny;

        }

    }

    private class SaveTaskListTask extends AsyncTask<List<TaskList>, Void, Void> {

        @Override
        protected Void doInBackground(List<TaskList>... params) {
            for (IPersister persister : persisters) {
                String filename = persister.getDefaultFilename();
                OutputStream os = null;

                NookOrCranny[] nooksAndCrannies = defaultNooksAndCrannies(filename);

                for (NookOrCranny noc : nooksAndCrannies) {

                    os = noc.fetchMeAnOutputStream();

                    if (os == null) continue;

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

}
