package com.luchenlabs.fantaskulous.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.app.TaskListFragment;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<TaskListFragment> _fragments;

    public TaskListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        _fragments = new ArrayList<TaskListFragment>(10);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.FragmentPagerAdapter#finishUpdate(android.view
     * .ViewGroup)
     */
    @Override
    public void finishUpdate(ViewGroup container) {
        // ((ViewGroup) container.getParent()).removeView(container);
        super.finishUpdate(container);
    }

    @Override
    public int getCount() {
        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists == null)
            return 0;
        return taskLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        while (position >= _fragments.size()) {
            _fragments.add(new TaskListFragment());
        }
        TaskListFragment fragment = _fragments.get(position);
        Bundle args = new Bundle();
        args.putInt(TaskListFragment.ARG_TASKLIST, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TaskList taskList = G.getState().getTaskLists().get(position);
        return taskList == null ? "Unknown" : taskList.getName(); // TODO
    }
}