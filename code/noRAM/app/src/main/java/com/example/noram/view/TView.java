package com.example.noram.view;

/**
 * Interface for Views to inherit from so that they can be updated by a model.
 * @param <M> Type of model class.
 */
public interface TView<M> {
    /**
     * Update the view with the new data from the model
     * @param model model data
     */
    void update(M model);
}
