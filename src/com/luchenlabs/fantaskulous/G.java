package com.luchenlabs.fantaskulous;

import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;

/**
 * Globals
 * 
 * @author cheezmeister
 * 
 */
public class G {
    public static class State {

        private FantaskulousModel model;

        private final MainController mainController;
        private final TaskListController taskListController;
        private final TaskController taskController;

        /**
         * Ctor
         */
        public State() {
            this.mainController = new MainController();
            this.taskListController = new TaskListController();
            this.taskController = new TaskController();
        }

        /**
         * @return the mainController
         */
        public MainController getMainController() {
            return mainController;
        }

        /**
         * @return the global data model
         */
        public FantaskulousModel getModel() {
            return this.model;
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
         * Set global data model
         * 
         * @param model
         */
        public void setModel(FantaskulousModel model) {
            this.model = model;

        }

    }

    private static State _state;

    public static State getState() {
        if (_state == null)
            _state = new State();
        return _state;
    }

}
