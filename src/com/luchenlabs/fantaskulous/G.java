package com.luchenlabs.fantaskulous;

import java.util.List;

import com.luchenlabs.fantaskulous.model.TaskList;

/**
 * Globals
 * 
 * @author cheezmeister
 * 
 */
public class G {
    private static State _state;

    public static State getState() {
        if (_state == null)
            _state = new State();
        return _state;
    }

    public static class State {
        private List<TaskList> taskLists;

        public List<TaskList> getTaskLists() {
            return taskLists;
        }

        public void setTaskLists(List<TaskList> taskLists) {
            this.taskLists = taskLists;
        }
    }

}
