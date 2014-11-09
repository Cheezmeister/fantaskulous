package com.luchenlabs.fantaskulous.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.TaskLists;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;

public class MainActivity extends AbstractActivity {

    private static final int CODE_IMPORT = 3456;

    private TaskListFragmentPagerAdapter _pagerAdapter;

    private ViewPager _viewPager;

    private View _spinner;

    private MainController _controller;

    private final IPersister persister = new JsonPersister();

    private void createList() {
        showTextInputDialog(
                getString(R.string.new_list),
                getString(R.string.list_name),
                new StringRunnable() {
                    @Override
                    public void run(String string) {
                        TaskList list = _controller.createTaskList(G.getState().getTaskLists(), string);
                        if (list != null) {
                            _pagerAdapter.presentNewList(list);
                            _viewPager.refreshDrawableState();
                        }
                    }
                });
    }

    private void deleteCurrentList() {
        int position = _viewPager.getCurrentItem();
        CharSequence name = _pagerAdapter.getPageTitle(position);
        if (_controller.removeTaskList(G.getState().getTaskLists(), name)) {
            _pagerAdapter.destroyItem(_viewPager, position);
            _viewPager.setAdapter(_pagerAdapter);
            refresh();
        }
    }

    private void export() {
        TaskLists lol = new TaskLists();
        lol.lists = (ArrayList<TaskList>) G.getState().getTaskLists();
        Intent send = new Intent();
        send.setAction(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, JsonPersister.getJSON(lol));
        send.setType("text/json"); //$NON-NLS-1$
        startActivity(Intent.createChooser(send, getString(R.string.export_tasks)));
    }

    private void finishOnCreate(List<TaskList> result) {
        _spinner.setVisibility(View.GONE);
        _controller = G.getState().getMainController();
        refresh();
    }

    private void handleTasksLoaded(List<TaskList> result) {
        G.getState().setTaskLists(result);
        finishOnCreate(result);
    }

    private void importFuckJavaKeywords() {
        Intent get = new Intent(Intent.ACTION_GET_CONTENT);
        get.setType("text/json"); //$NON-NLS-1$
        startActivityForResult(get, CODE_IMPORT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CODE_IMPORT)
            return;

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                ContentResolver cr = getContentResolver();
                try {
                    InputStream is = cr.openInputStream(uri);
                    List<TaskList> tl = persister.load(is);
                    this.handleTasksLoaded(tl);
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

        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists == null) {
            _spinner.setVisibility(View.VISIBLE);
            Log.i(getClass().getSimpleName(), "Kicking off load task"); //$NON-NLS-1$
            new LoadTaskListTask().execute();
        } else {
            Log.i(getClass().getSimpleName(), "Finishing on create with " + taskLists.size() + "lists"); //$NON-NLS-1$ //$NON-NLS-2$
            finishOnCreate(taskLists);
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
            case R.id.action_create:
                createList();
                break;
            case R.id.action_remove:
                deleteCurrentList();
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
                _controller.removeAllCompletedTasks(G.getState().getTaskLists());
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

    @SuppressWarnings("unchecked")
    private void saveTasks() {
        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists != null) {
            new SaveTaskListTask().execute(taskLists);
        }
    }

    private class LoadTaskListTask extends AsyncTask<Void, Void, List<TaskList>> {
        String filename = C.TASK_FILE;

        private List<TaskList> attemptLoad(InputStream is) {
            if (is != null) {
                try {
                    return persister.load(is);
                } catch (JsonParseException e) {
                    Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                }
            }
            return null;
        }

        @Override
        protected List<TaskList> doInBackground(Void... params) {
            InputStream is = null;
            try {
                is = openFileInput(filename);
            } catch (FileNotFoundException e) {
                Log.w(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename, e));
            }
            List<TaskList> lists = null;

            lists = attemptLoad(is);

            if (lists != null)
                return lists;

            try {
                AssetManager assetManager = getAssets();
                is = assetManager.open(filename);
            } catch (IOException e) {
                Log.wtf(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename, e));
            }

            lists = attemptLoad(is);

            if (lists == null)
                return new ArrayList<TaskList>();

            return lists;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<TaskList> result) {
            super.onPostExecute(result);

            handleTasksLoaded(result);
        }

    }

    private class SaveTaskListTask extends AsyncTask<List<TaskList>, Void, Void> {

        @Override
        protected Void doInBackground(List<TaskList>... params) {
            String filename = C.TASK_FILE;
            OutputStream os = null;

            try {
                os = openFileOutput(filename, MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, filename, e));
            }

            ArrayList<TaskList> lists = new ArrayList<TaskList>(params[0]);

            try {
                persister.save(os, lists);
                os.close();
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, filename, e));
            } catch (Exception e) {
                Log.wtf(getClass().getSimpleName(), String.format("Unexpected exception %s", e.toString())); //$NON-NLS-1$
            }
            return null;
        }

    }

}
