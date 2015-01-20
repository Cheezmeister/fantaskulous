package com.luchenlabs.fkls;

import com.luchenlabs.fkls.app.storage.NookOrCranny;
import com.luchenlabs.fkls.controller.MainController;
import com.luchenlabs.fkls.controller.TaskController;
import com.luchenlabs.fkls.controller.TaskListController;
import com.luchenlabs.fkls.model.FantaskulousModel;

/**
 * Globals
 * 
 * @author cheezmeister
 * 
 */
public class G {
    public static class State {

        private FantaskulousModel model;
        private NookOrCranny dataSource;

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

        public NookOrCranny getDataSource() {
            return this.dataSource;
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
         * Set the data source we loaded from
         * 
         * @param nookOrCranny
         */
        public void setDataSource(NookOrCranny nookOrCranny) {
            this.dataSource = nookOrCranny;
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
