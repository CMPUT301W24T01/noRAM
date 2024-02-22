package com.example.noram.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Class to represent an Event
 */
public class Event {
    private int id;
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private ArrayList<Integer> milestones;
    private Photo poster;
    private Photo checkInQR;
    private Photo promoQR;
    private boolean trackLocation;

    // Constructors
    /**
     * Default constructor
     */
    public Event() {}

    /**
     * Constructor for all non-photo attributes (photos are initialized via default constructor)
     * @param id unique ID number for event
     * @param name name of event
     * @param location location of event
     * @param startTime date and time (year, month, day, hour, minute) of event start
     * @param endTime date and time (year, month, day, hour, minute) of event end
     * @param details paragraph of event details
     * @param milestones list of attendance milestones to track
     * @param trackLocation is location tracking of check-ins enabled
     */
    public Event(int id, String name, String location, LocalDateTime startTime, LocalDateTime endTime, String details, ArrayList<Integer> milestones, boolean trackLocation) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.poster = new Photo();
        this.checkInQR = new Photo();
        this.promoQR = new Photo();
        this.trackLocation = trackLocation;
    }

    /**
     * Constructor for all attributes
     * @param id unique ID number for event
     * @param name name of event
     * @param location location of event
     * @param startTime date and time (year, month, day, hour, minute) of event start
     * @param endTime date and time (year, month, day, hour, minute) of event end
     * @param details paragraph of event details
     * @param milestones list of attendance milestones to track
     * @param poster poster for event
     * @param checkInQR QR code used to check user in to event
     * @param promoQR QR code used to promote the event
     * @param trackLocation is location tracking of check-ins enabled
     */
    public Event(int id, String name, String location, LocalDateTime startTime, LocalDateTime endTime, String details, ArrayList<Integer> milestones, Photo poster, Photo checkInQR, Photo promoQR, boolean trackLocation) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.poster = poster;
        this.checkInQR = checkInQR;
        this.promoQR = promoQR;
        this.trackLocation = trackLocation;
    }

    // Getters
    /**
     * Returns id of event
     * @return id attribute
     */
    public int getId() {
        return id;
    }

    /**
     * Returns name of event
     * @return name attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Returns location of event
     * @return location attribute
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns start time of event
     * @return startTime attribute
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns end time of event
     * @return endTime attribute
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns details of event
     * @return details attribute
     */
    public String getDetails() {
        return details;
    }

    /**
     * Returns ArrayList of attendance milestones of event
     * @return milestones attribute
     */
    public ArrayList<Integer> getMilestones() {
        return milestones;
    }

    /**
     * Returns poster Photo of event
     * @return poster attribute
     */
    public Photo getPoster() {
        return poster;
    }

    /**
     * Returns Photo of check-in QR code
     * @return checkInQR attribute
     */
    public Photo getCheckInQR() {
        return checkInQR;
    }

    /**
     * Returns Photo of promotional QR code
     * @return promoQR attribute
     */
    public Photo getPromoQR() {
        return promoQR;
    }

    /**
     * Returns true if location tracking is enabled, false otherwise
     * @return trackLocation attribute
     */
    public boolean isTrackLocation() {
        return trackLocation;
    }

    // Setters

    /**
     * Set id of event
     * @param id new id for event
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set name of event
     * @param name new name for event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set location of event
     * @param location new location for event
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set start time of event
     * @param startTime new startTime for event
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Set end time of event
     * @param endTime new end time for event
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Set details of events
     * @param details new details for event
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Set milestones of event
     * @param milestones new milestones for event
     */
    public void setMilestones(ArrayList<Integer> milestones) {
        this.milestones = milestones;
    }

    /**
     * Set poster of event
     * @param poster new poster for event
     */
    public void setPoster(Photo poster) {
        this.poster = poster;
    }

    /**
     * Set check-in QR code of event
     * @param checkInQR new checkInQR for event
     */
    public void setCheckInQR(Photo checkInQR) {
        this.checkInQR = checkInQR;
    }

    /**
     * Set promotional QR code of event
     * @param promoQR new promoQR for event
     */
    public void setPromoQR(Photo promoQR) {
        this.promoQR = promoQR;
    }

    /**
     * Set whether or not check-in location is tracked
     * @param trackLocation new trackLocation for event
     */
     public void changeTrackLocation(boolean trackLocation) {
        this.trackLocation = trackLocation;
     }

     // Functions
}
