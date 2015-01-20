package com.luchenlabs.fkls.view;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.app.TaskListFragment;
import com.luchenlabs.fkls.model.FklsModel;
import com.luchenlabs.fkls.model.TaskList;

public class TaskListFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<TaskListFragment> _fragments;
    private TaskList _newList;

    public TaskListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        _fragments = new ArrayList<TaskListFragment>(10);
    }

    public void destroyItem(ViewPager viewPager, int position) {
        TaskListFragment taskListFragment = _fragments.get(position);
        if (taskListFragment == null) throw new IllegalStateException("No fragment at position " + position);
        super.destroyItem(viewPager, position, taskListFragment);
    }

    @Override
    public int getCount() {
        FklsModel model = G.getState().getModel();
        if (model == null) return 0;
        return model.taskLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        Log.v(getClass().getSimpleName(), "getItem called for position " + position); //$NON-NLS-1$
        while (position >= _fragments.size()) {
            _fragments.add(new TaskListFragment());
        }
        TaskListFragment fragment = _fragments.get(position);
        Bundle args = new Bundle();
        args.putInt(TaskListFragment.ARG_TASKLIST, position);
        FklsModel model = G.getState().getModel();
        if (model != null && _newList != null && _newList == model.taskLists.get(position)) {
            args.putBoolean(TaskListFragment.ARG_IS_NEW, true);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        Log.v(getClass().getSimpleName(), "getItemPos called for object " + object); //$NON-NLS-1$
        int i = _fragments.indexOf(object);
        return (i < 0) ? POSITION_NONE : i;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
     */
    @Override
    public CharSequence getPageTitle(int position) {
        FklsModel model = G.getState().getModel();
        if (model == null) {
            Log.w(getClass().getSimpleName(), "Title for null list at position " + position); //$NON-NLS-1$
            return "Unknown"; //$NON-NLS-1$
        }
        // TODO fancy interactive title setting by poking title
        return model.taskLists.get(position).toString();
    }

    /**
     * Open up a new list that the user has created
     *
     * @param list
     */
    public void presentNewList(TaskList list) {
        _newList = list;
        refresh();
    }

    public void refresh() {
        notifyDataSetChanged();
        for (TaskListFragment fragment : _fragments) {
            fragment.refresh();
        }
    }
}