package com.luchenlabs.fantaskulous.view;

public interface FView<T> {

    /**
     * Force an update from the model
     */
    void refresh();

    /**
     * Assign a model to this view
     * 
     * @param model
     */
    void setModel(T model);

}
