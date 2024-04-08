/*
This file is used to create the Notification class. This class is used to create notifications that can be displayed to the user.
Outstanding Issues:
- None
 */

package com.example.noram.model;

/**
 * The Notification class is used to create notifications that can be displayed to the user.
 * @maintainer Christiaan
 * @author Christiaan
 */
public class Notification {

    private final String title;
    private final String content;

    /**
     * Create a new notification with a title and content.
     * @param title the title of the notification
     * @param content the content of the notification
     */
    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Get the content of the notification.
     * @return the content of the notification
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the title of the notification.
     * @return the title of the notification
     */
    public String getTitle() {
        return title;
    }
}
