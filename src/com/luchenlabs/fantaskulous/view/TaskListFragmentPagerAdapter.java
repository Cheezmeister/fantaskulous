package com.luchenlabs.fantaskulous.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.app.TaskListFragment;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<TaskListFragment> _fragments;
    private TaskList _newList;

    public TaskListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        _fragments = new ArrayList<TaskListFragment>(10);
    }

    @Override
    public int getCount() {
        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists == null)
            return 0;
        Log.v(getClass().getSimpleName(), "getCount called, giving " + taskLists.size()); //$NON-NLS-1$
        return taskLists.size();
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
        if (_newList != null && _newList == G.getState().getTaskLists().get(position)) {
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
        TaskList taskList = G.getState().getTaskLists().get(position);
        if (taskList == null) {
            Log.w(getClass().getSimpleName(), "Title for null list at position " + position); //$NON-NLS-1$
            return "Unknown"; //$NON-NLS-1$
        }
        return taskList.getName(); // TODO fancy interactive title setting
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
        for (TaskListFragment fragment : _fragments) {
            fragment.refresh();
        }
        notifyDataSetChanged();
    }
}