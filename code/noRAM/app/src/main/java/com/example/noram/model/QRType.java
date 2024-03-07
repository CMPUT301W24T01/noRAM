/*
This file is used to define the different types of QR codes that can be used in the app.
Outstanding Issues:
- None
 */

package com.example.noram.model;

/**
 * Enumerator for different QR code types
 * @maintainer Cole
 * @author Cole
 */
public enum QRType {
    /**
     * A QR Code for signing into an event
     */
    SIGN_IN,

    /**
     * A Promotional QR code
     */
    PROMOTIONAL,
}
