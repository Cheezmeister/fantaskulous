package com.luchenlabs.fkls.model;

/**
 * A {@link TaskList} which uses a sigil to denote its type. Specifically,
 * {@link TaskProject +projects} and {@link TaskContext @contexts} implement
 * this class.
 *
 * TODO Collapse this hierarchy to one concrete class
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

    @Override
    public String getName() {
        return getSigil() + super.getName();
    }

    protected abstract char getSigil();

    @Override
    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be null"); //$NON-NLS-1$
        if (name.contains(String.valueOf(getSigil()))) {
            String fmt = "%s name %s cannot contain %c"; //$NON-NLS-1$
            String msg = String.format(fmt, getClass().getName(), name, getSigil());
            throw new IllegalArgumentException(msg);
        }
        super.setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }

}