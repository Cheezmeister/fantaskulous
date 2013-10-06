package com.luchenlabs.fantaskulous.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import com.luchenlabs.fantaskulous.Persister;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

            if (is == null) {
                try {
                    AssetManager assetManager = getAssets();
                    is = assetManager.open(filename);
                } catch (IOException e) {
                    Log.wtf(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename, e));
                }
            }

            if (is == null)
                return new ArrayList<TaskList>(10);

            List<TaskList> lists = null;
            try {
                lists = Persister.load(is);
            } catch (JsonParseException e) {
                Log.wtf(getClass().getSimpleName(), getString(R.string.fmt_not_found, filename));
            }

            if (lists == null)
                return new ArrayList<TaskList>(10);

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

}
