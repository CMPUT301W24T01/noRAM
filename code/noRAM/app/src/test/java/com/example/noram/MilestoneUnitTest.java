/*
 * This file is for testing the Milestone class.
 */
package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.noram.model.Milestone;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Milestone class.
 * @maintainer Ethan
 * @author Ethan
 */
public class MilestoneUnitTest {
    /**
     * Test the equals method of the Milestone class.
     */
    @Test
    public void equalsTest() {
        Milestone milestone1 = new Milestone(3, 3);
        Milestone milestone2 = new Milestone(3, 3);
        Milestone milestone3 = new Milestone(2, 2);
        Milestone milestone4 = new Milestone(3, 2);

        assertTrue(milestone1.equals(milestone2));
        assertFalse(milestone1.equals(milestone3));
        assertFalse(milestone1.equals(milestone4));
        assertFalse(milestone3.equals(milestone4));
    }

    /**
     * Test the compareTo method of the Milestone class.
     */
    @Test
    public void compareToTest() {
        Milestone milestone1 = new Milestone(3, 3);
        Milestone milestone2 = new Milestone(3, 3);
        Milestone milestone3 = new Milestone(2, 2);
        Milestone milestone4 = new Milestone(3, 2);
        Milestone milestone5 = new Milestone(2, 3);

        assertTrue(milestone1.compareTo(milestone2) == 0);
        assertTrue(milestone1.compareTo(milestone3) > 0);
        assertTrue(milestone1.compareTo(milestone4) == 0);
        assertTrue(milestone3.compareTo(milestone4) < 0);
        assertTrue(milestone4.compareTo(milestone5) > 0);
    }

    /**
     * Test the hashCode method of the Milestone class.
     */
    @Test
    public void hashCodeTest() {
        Milestone milestone1 = new Milestone(3, 3);
        Milestone milestone2 = new Milestone(3, 3);
        Milestone milestone3 = new Milestone(2, 2);
        Milestone milestone4 = new Milestone(3, 2);
        Milestone milestone5 = new Milestone(2, 3);
        Milestone milestone6 = new Milestone(1, 2);
        Milestone milestone7 = new Milestone(125, 1);


        assertEquals(milestone1.hashCode(), milestone2.hashCode());
        assertNotEquals(milestone1.hashCode(), milestone3.hashCode());
        assertNotEquals(milestone1.hashCode(), milestone4.hashCode());
        assertNotEquals(milestone3.hashCode(), milestone4.hashCode());
        assertNotEquals(milestone4.hashCode(), milestone5.hashCode());
    }
}
