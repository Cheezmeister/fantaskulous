package com.luchenlabs.fantaskulous.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;

public class MainActivity extends FragmentActivity {

    protected static void handleTasksLoaded(List<TaskList> result) {
        G.getState().setTaskLists(result);
    }

    TaskListFragmentPagerAdapter _pagerAdapter;

    ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        _pagerAdapter = new TaskListFragmentPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.pager);
        _viewPager.setAdapter(_pagerAdapter);

        new LoadTaskListTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onStop() {
        new SaveTaskListTask().execute(G.getState().getTaskLists());
        super.onStop();
    }

    @SuppressWarnings("unchecked")
    protected void saveTasks() {
        new SaveTaskListTask().execute(G.getState().getTaskLists());
    }

    private class LoadTaskListTask extends AsyncTask<Void, Void, List<TaskList>> {
        @Override
        protected List<TaskList> doInBackground(Void... params) {
            String filename = C.TASK_FILE;
            InputStream is = null;
            try {
                is = openFileInput(filename);
            } catch (FileNotFoundException e) {
                Log.w(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename, e));
            }

            List<TaskList> lists = null;
            if (is != null) {
                try {
                    lists = JsonPersister.load(is);
                } catch (JsonParseException e) {
                    Log.e(getClass().getSimpleName(), getString(R.string.fmt_invalid_json, filename));
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), getString(R.string.fmt_invalid_json, filename));
                }
            }
            if (lists == null) {
                try {
                    AssetManager assetManager = getAssets();
                    is = assetManager.open(filename);
                } catch (IOException e) {
                    Log.wtf(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename, e));
                }
                if (is != null) {
                    try {
                        lists = JsonPersister.load(is);
                    } catch (JsonParseException e) {
                        Log.e(getClass().getSimpleName(), getString(R.string.fmt_invalid_json, filename));
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), getString(R.string.fmt_invalid_json, filename));
                    }
                }
            }

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
                JsonPersister.save(os, lists);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, filename, e));
            }
            return null;
        }

    }

}
