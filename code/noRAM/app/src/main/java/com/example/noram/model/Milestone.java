/*
 * File to represent the milestone object
 * Outstanding issues: None
 */
package com.example.noram.model;

/**
 * Class to represent a milestone
 * @maintainer Ethan
 * @author Ethan
 */
public class Milestone {
    private Integer milestone;
    private Integer progress;

    /**
     * THis is the constructor for a milestone object
     * @param milestone the milestone
     * @param progress the progress toward the milestone
     */
    public Milestone(Integer milestone, Integer progress) {
        this.milestone = milestone;
        this.progress = progress;
    }

    /**
     * Returns the milestone of the Milestone object
     * @return the milestone of the Milestone object
     */
    public Integer getMilestone() {
        return milestone;
    }

    /**
     * Returns the progress of the Milestone object
     * @return the progress of the Milestone object
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * Sets the milestone of the Milestone object
     * @param milestone the milestone to set
     */
    public void setMilestone(Integer milestone) {
        this.milestone = milestone;
    }

    /**
     * Sets the progress of the Milestone object
     * @param progress the progress to set
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * Compares the Milestone object to another object
     * @param o the object to compare to
     * @return the comparison of the Milestone object to the other object
     */
    public int compareTo(Object o) {
        Milestone other = (Milestone) o;
        return this.milestone.compareTo(other.milestone);
    }

    /**
     * Returns whether the Milestone object is equal to another object
     * @param o the object to compare to
     * @return whether the Milestone object is equal to the other object
     */
    @Override
    public boolean equals(Object o) {
        Milestone other = (Milestone) o;
        return this.milestone.equals(other.milestone) && this.progress.equals(other.progress);
    }

    /**
     * Returns the hash code of the Milestone object, don't worry about collision too much because
     * progress will always be the same for every comparison
     * @return the hash code of the Milestone object
     */
    @Override
    public int hashCode() {
        return milestone.hashCode() + 31 * (progress.hashCode());
    }
}
