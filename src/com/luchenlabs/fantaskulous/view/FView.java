package com.luchenlabs.fantaskulous.view;


public interface FView<T> {

    /**
     * Force an update from the model
     */
    void refresh();

    void setModel(T model);

}
