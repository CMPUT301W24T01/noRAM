package com.example.noram.model;

import com.example.noram.view.TView;

import java.util.ArrayList;

/**
 * Class to use as a model in the MVC design pattern
 * @param <V> View class the model works with.
 */
public class ModelBase<V extends TView<ModelBase<?>>> {

    /**
     * List of views associated with the model.
     */
    private ArrayList<V> views;

    /**
     * Create a ModelBase instance.
     */
    public ModelBase() {
        views = new ArrayList<>();
    }

    /**
     * Add a view to the model.
     * @param view View to add.
     */
    public void addView(V view) {
        if (!views.contains(view)) {
            views.add(view);
        }
    }

    /**
     * Delete a view from the model.
     * @param view view to delete.
     */
    public void deleteView(V view) {
        views.remove(view);
    }

    /**
     * Notify to all the model's views that the data has changed.
     */
    public void notifyChanged() {
        for (V view : views) {
            view.update(this);
        }
    }

}
