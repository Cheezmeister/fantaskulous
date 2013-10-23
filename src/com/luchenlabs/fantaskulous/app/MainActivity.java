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
import android.view.View;

import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;

public class MainActivity extends FragmentActivity {

    private TaskListFragmentPagerAdapter _pagerAdapter;

    private ViewPager _viewPager;

    private View _spinner;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _pagerAdapter = new TaskListFragmentPagerAdapter(
                getSupportFragmentManager());

        _viewPager = (ViewPager) findViewById(R.id.pager);
        _viewPager.setAdapter(_pagerAdapter);

        if (G.getState().getTaskLists() == null) {
            new LoadTaskListTask().execute();
        }
        _spinner = findViewById(R.id.spinner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onStop() {
        saveTasks();
        super.onStop();
    }

    private String ex(Exception e, int resId, Object... args) {
        return getString(resId, args) + "\n" //$NON-NLS-1$
                + getString(R.string.fmt_the_exception_thrown_was, e.toString());
    }

    private void handleTasksLoaded(List<TaskList> result) {
        G.getState().setTaskLists(result);
        _spinner.setVisibility(View.GONE);
        _viewPager.getAdapter().notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    private void saveTasks() {
        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists != null) {
            new SaveTaskListTask().execute(taskLists);
        }
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
                    Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
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
                        Log.wtf(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
                    } catch (Exception e) {
                        Log.wtf(getClass().getSimpleName(), ex(e, R.string.fmt_invalid_json, filename));
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
                os.close();
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), getString(R.string.fmt_access_denied, filename, e));
            } catch (Exception e) {
                Log.wtf(getClass().getSimpleName(), String.format("Unexpected exception %s", e.toString()));
            }
            return null;
        }

    }

}
