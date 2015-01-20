package com.luchenlabs.fkls.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.luchenlabs.fkls.DumbCallback;
import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.IPersister;
import com.luchenlabs.fkls.JsonPersister;
import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.app.storage.LoadTaskListTask;
import com.luchenlabs.fkls.app.storage.SaveTaskListTask;
import com.luchenlabs.fkls.app.storage.LoadTaskListTask.LoadResult;
import com.luchenlabs.fkls.controller.MainController;
import com.luchenlabs.fkls.model.FklsModel;
import com.luchenlabs.fkls.model.TaskList;
import com.luchenlabs.fkls.view.TaskListFragmentPagerAdapter;

public class MainActivity extends AbstractActivity {

    private static final int CODE_IMPORT = 3456;

    private TaskListFragmentPagerAdapter _pagerAdapter;
    private ViewPager _viewPager;
    private View _spinner;

    private MainController _controller;

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

    private void export() {
        Intent send = new Intent(Intent.ACTION_SEND);
        FklsModel model = G.getState().getModel();
        if (model == null) {
            Log.wtf(getClass().getSimpleName(), "No model found"); //$NON-NLS-1$
        }
        send.putExtra(Intent.EXTRA_TEXT, JsonPersister.getJSON((model.taskLists)));
        send.setType("text/json"); //$NON-NLS-1$
        startActivity(Intent.createChooser(send, getString(R.string.export_tasks)));
    }

    private void finishOnStart(FklsModel model) {
        _controller = G.getState().getMainController();

        handleNewDataLoaded(model);

        _viewPager.setVisibility(View.VISIBLE);
        _spinner.setVisibility(View.GONE);
    }

    void handleFinishedLoading(LoadTaskListTask.LoadResult result) {
        FklsModel model = result.model;
        G.getState().setDataSource(result.nookOrCranny);
        finishOnStart(model);
    }

    private void handleNewDataLoaded(FklsModel model) {
        G.getState().getMainController().sortAll(model.taskLists);
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
        FklsModel model = G.getState().getModel();
        if (model == null) {
            _spinner.setVisibility(View.VISIBLE);
            Log.i(getClass().getSimpleName(), "Kicking off load task"); //$NON-NLS-1$
            LoadTaskListTask async = new LoadTaskListTask(this, new DumbCallback<LoadResult>() {
                @Override
                public void call(LoadResult result) {
                    handleFinishedLoading(result);
                }
            });
            async.execute();
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
        FklsModel model = G.getState().getModel();
        if (model != null) {
            new SaveTaskListTask(this, null).execute(model.taskLists);
        }
    }

}
