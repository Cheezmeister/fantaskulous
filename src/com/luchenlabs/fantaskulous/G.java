package com.luchenlabs.fantaskulous;

import java.util.List;

import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.controller.TaskListController;
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
        private MainController mainController;
        private TaskListController taskListController;
        private TaskController taskController;

        /**
         * @return the mainController
         */
        public MainController getMainController() {
            return mainController;
        }

        /**
         * @return the taskController
         */
        public TaskController getTaskController() {
            return taskController;
        }

        /**
         * @return the taskListController
         */
        public TaskListController getTaskListController() {
            return taskListController;
        }

        /**
         * This is the top level model for the app
         * 
         * @return top level model for the app
         */
        public List<TaskList> getTaskLists() {
            return taskLists;
        }

        public void setTaskLists(List<TaskList> taskLists) {
            this.taskLists = taskLists;
            this.mainController = new MainController();
            this.taskListController = new TaskListController();
            this.taskController = new TaskController();
        }
    }

}
