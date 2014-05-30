package com.luchenlabs.fantaskulous.model;

/**
 * A {@link TaskList} which uses a sigil to denote its type. Specifically,
 * {@link TaskProject +projects} and {@link TaskContext @contexts} implement
 * this class.
 * 
 * @author cheezmeister
 */
public abstract class SigillyTaskList extends TaskList {

    public SigillyTaskList() {
        super();
    }

    public SigillyTaskList(String name) {
        super(name);
    }

    protected abstract char getSigil();

    @Override
    public void setName(String name) {
        if (name.contains(String.valueOf(getSigil()))) {
            String fmt = "%s name %s cannot contain %c"; //$NON-NLS-1$
            String msg = String.format(fmt, getClass().getName(), name, getSigil());
            throw new IllegalArgumentException(msg);
        }
        super.setName(name);
    }

    @Override
    public String toString() {
        return getSigil() + this.getName();
    }

}