package com.luchenlabs.fantaskulous.model;


public class TaskContext extends SigillyTaskList {

    static final char SIGIL = '@';

    public TaskContext(String ctx) {
        super(ctx);
    }

    @Override
    public void addTask(Task task) {
        task.getContexts().add(this);
        super.addTask(task);
    }

    @Override
    protected char getSigil() {
        return SIGIL;
    }

}
