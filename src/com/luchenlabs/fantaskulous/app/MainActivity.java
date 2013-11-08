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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonParseException;
import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListFragmentPagerAdapter;
import com.luchenlabs.fantaskulous.view.TaskView;

public class MainActivity extends AbstractActivity {

    private TaskListFragmentPagerAdapter _pagerAdapter;

    private ViewPager _viewPager;

    private View _spinner;

    private MainController _controller;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_move_to_list:
                // TODO this
        }
        return super.onContextItemSelected(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     * android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = new MenuInflater(this);
        if (v instanceof TaskView) {
            mi.inflate(R.menu.context_task, menu);
            menu.add("stuff");
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
                removeList();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
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
            new LoadTaskListTask().execute();
        } else {
            finishOnCreate(taskLists);
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

    private void createList() {
        showTextInputDialog(
                getString(R.string.new_list),
                getString(R.string.list_name),
                new StringRunnable() {
                    @Override
                    public void run(String string) {
                        TaskList list = _controller.createTaskList(string);
                        if (list != null) {
                            _pagerAdapter.presentNewList(list);
                            _viewPager.refreshDrawableState();
                        }
                    }
                });
    }

    private void finishOnCreate(List<TaskList> result) {
        _controller = new MainController(result);
        _spinner.setVisibility(View.GONE);
        _viewPager.getAdapter().notifyDataSetChanged();
        // ((ListView)
        // findViewById(R.id.taskListListView)).setOnItemLongClickListener(new
        // OnItemLongClickListener() {
        // @Override
        // public boolean onItemLongClick(AdapterView<?> parent, View view, int
        // position, long id) {
        // registerForContextMenu(view);
        // return false;
        // }
        // });
    }

    private void handleTasksLoaded(List<TaskList> result) {
        G.getState().setTaskLists(result);
        finishOnCreate(result);
    }

    private void removeList() {
        int position = _viewPager.getCurrentItem();
        CharSequence name = _pagerAdapter.getPageTitle(position);
        _controller.removeTaskList(name);
        _pagerAdapter.refresh(); // TODO register observer
        _viewPager.setAdapter(_pagerAdapter);
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
                Log.wtf(getClass().getSimpleName(), String.format("Unexpected exception %s", e.toString())); //$NON-NLS-1$
            }
            return null;
        }

    }

}
