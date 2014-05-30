package com.luchenlabs.fantaskulous.model;


public class TaskProject extends SigillyTaskList {

    private static final char SIGIL = '+';

    public TaskProject(String prj) {
        super(prj);
    }

    @Override
    public void addTask(Task task) {
        task.getProjects().add(this);
        super.addTask(task);
    }

    @Override
    protected char getSigil() {
        return SIGIL;
    }
}
