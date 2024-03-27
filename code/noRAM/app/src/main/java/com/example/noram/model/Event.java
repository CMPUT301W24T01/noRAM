/*
This file is used to create an event object. It contains all the attributes of an event and the methods to get and set them.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import androidx.annotation.Nullable;

import com.example.noram.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class to represent an Event
 * @maintainer Carlin
 * @author Carlin
 * @author Cole
 * @author Ethan
 */
public class Event {
    private String id;
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private ArrayList<Integer> milestones;
    private QRCode checkInQR;
    private QRCode promoQR;
    private boolean trackLocation;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<String> checkedInAttendees;
    private List<String> signedUpAttendees;
    private Long signUpLimit;

    /**
     * Default constructor for Event
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
     * @param signUpLimit number of signups for event allowed (-1 for no limit)
     */
    public Event(
            String id,
            String name,
            String location,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String details,
            ArrayList<Integer> milestones,
            boolean trackLocation,
            Long signUpLimit) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.checkInQR = new QRCode(this.id + "-event", this.id, QRType.SIGN_IN);
        this.promoQR = new QRCode(this.id + "-promo", this.id, QRType.PROMOTIONAL);
        this.trackLocation = trackLocation;
        this.checkedInAttendees = new ArrayList<>();
        this.signedUpAttendees = new ArrayList<>();
        this.signUpLimit = signUpLimit;
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
     * @param checkInQR QR code used to check user in to event
     * @param promoQR QR code used to promote the event
     * @param trackLocation is location tracking of check-ins enabled
     * @param checkedInAttendees list of checked in attendees
     * @param signedUpAttendees list of signed up attendees
     * @param signUpLimit number of signups for event allowed (-1 for no limit)
     */
    public Event(
            String id,
            String name,
            String location,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String details,
            ArrayList<Integer> milestones,
            QRCode checkInQR,
            QRCode promoQR,
            boolean trackLocation,
            List<String> checkedInAttendees,
            List<String> signedUpAttendees,
            Long signUpLimit) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.checkInQR = checkInQR;
        this.promoQR = promoQR;
        this.trackLocation = trackLocation;
        this.checkedInAttendees = checkedInAttendees;
        this.signedUpAttendees = signedUpAttendees;
        this.signUpLimit = signUpLimit;
    }

    // Getters
    /**
     * Returns id of event
     * @return id attribute
     */
    public String getId() {
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
     * Returns photo of check-in QR code
     * @return checkInQR attribute
     */
    public QRCode getCheckInQR() {
        return checkInQR;
    }

    /**
     * Returns photo of promotional QR code
     * @return promoQR attribute
     */
    public QRCode getPromoQR() {
        return promoQR;
    }

    /**
     * Returns true if location tracking is enabled, false otherwise
     * @return trackLocation attribute
     */
    public boolean isTrackLocation() {
        return trackLocation;
    }

    /**
     * Get the list of checked in attendees
     * @return list of attendee identifiers
     */
    public List<String> getCheckedInAttendees() {
        return checkedInAttendees;
    }

    /**
     * Get the list of signed up attendees
     * @return list of attendee identifiers
     */
    public List<String> getSignedUpAttendees() {
        return signedUpAttendees;
    }

    /**
     * Get the sign up limit
     * @return sign up limit
     */
    public Long getSignUpLimit() {
        return signUpLimit;
    }

    /**
     * Return true if signups are limited (limit set to 0 or above), false otherwise
     * @return whether or not sign-ups are limited
     */
    public boolean isLimitedSignUps() {return signUpLimit >= 0;}

    /**
     * Returns the number of signed-up attendees
     * @return size of signedUpAttendees ArrayList
     */
    public int getSignUpCount() {
        if (signedUpAttendees == null) {
            return 0;
        }
        return signedUpAttendees.size();
    }

    // Setters
    /**
     * Set id of event
     * @param id new id for event
     */
    public void setId(String id) {
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
     * Set check-in QR code of event
     * @param checkInQR new checkInQR for event
     */
    public void setCheckInQR(QRCode checkInQR) {
        this.checkInQR = checkInQR;
    }

    /**
     * Set promotional QR code of event
     * @param promoQR new promoQR for event
     */
    public void setPromoQR(QRCode promoQR) {
        this.promoQR = promoQR;
    }

    /**
     * Set whether or not check-in location is tracked
     * @param trackLocation new trackLocation for event
     */
    public void setTrackLocation(boolean trackLocation) {
        this.trackLocation = trackLocation;
    }

    /**
     * Set the list of checked in attendees
     * @param checkedInAttendees new list of checked in attendees
     */
    public void setCheckedInAttendees(List<String> checkedInAttendees) {
        this.checkedInAttendees = checkedInAttendees;
    }

    /**
     * Sets the list of signed up attendees
     * @param signedUpAttendees new list of signed up attendees
     */
    public void setSignedUpAttendees(List<String> signedUpAttendees) {
        this.signedUpAttendees = signedUpAttendees;
    }

    /**
     * Sets sign up limit
     * @param signUpLimit new sign up limit
     */
    public void setSignUpLimit(Long signUpLimit) {
        this.signUpLimit = signUpLimit;
    }

    // Functions
    /**
     * Check for equality between an event and another object
     * @param obj object to check for equality
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    /**
     * Updates the event in the database
     */
    public void updateDBEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("location", location);
        data.put("startTime", startTime.format(formatter));
        data.put("endTime", endTime.format(formatter));
        data.put("details", details);
        data.put("milestones", milestones);
        data.put("checkInQR", checkInQR.getEncodedData());
        data.put("promoQR", promoQR.getEncodedData());
        data.put("trackLocation", trackLocation);
        data.put("checkedInAttendees", checkedInAttendees);
        data.put("signedUpAttendees", signedUpAttendees);
        data.put("signUpLimit", signUpLimit);
        MainActivity.db.getEventsRef().document(id).set(data);

        promoQR.updateDBQRCode();
        checkInQR.updateDBQRCode();
    }

    /**
     * Updates the event given a database 'event' document
     * @param doc The database document containing the fields used to update the event instance
     */
    public void updateWithDocument(DocumentSnapshot doc){
        this.setId(doc.getId());
        this.setName(doc.getString("name"));
        this.setDetails(doc.getString("details"));
        this.setLocation(doc.getString("location"));
        this.setTrackLocation(Boolean.TRUE.equals(doc.getBoolean("trackLocation")));
        this.setStartTime(LocalDateTime.parse(doc.getString("startTime"), formatter));
        this.setEndTime(LocalDateTime.parse(doc.getString("endTime"), formatter));
        this.setMilestones((ArrayList<Integer>) doc.get("milestones"));
        this.setCheckedInAttendees((List<String>) doc.get("checkedInAttendees"));
        this.setSignedUpAttendees((List<String>) doc.get("signedUpAttendees"));
        this.setSignUpLimit(doc.getLong("signUpLimit"));
        this.setPromoQR(new QRCode(doc.getString("promoQR"), this.getId(), QRType.PROMOTIONAL));
        this.setCheckInQR(new QRCode(doc.getString("checkInQR"), this.getId(), QRType.SIGN_IN));
    }

    /**
     * Adds string representation of attendee into signedUpAttendees list
     * @param attendee string representation of attendee
     */
    public void addSignedUpAttendee(String attendee) {
        signedUpAttendees.add(attendee);
    }

    /** Get the list of checked in attendees and the number of times they have checked in to provide to the callback
     * * @param callback the callback to provide the list of attendees and their check-in counts to
     */
    public void getCheckedInAttendeesAndCounts(Consumer<ArrayList<AttendeeCheckInCounter>> callback) {
        ArrayList<Attendee> checkedInAttendeeObjects = new ArrayList<>();
        MainActivity.db.getAttendeeRef().whereIn("identifier", checkedInAttendees).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                Map<String, Object> data = document.getData();
                Attendee attendee = new Attendee((String) data.get("identifier"));
                attendee.updateWithMap(data);
                checkedInAttendeeObjects.add(attendee);
            }

            ArrayList<AttendeeCheckInCounter> attendeeCheckInCounters = countCheckIns(checkedInAttendeeObjects);
            callback.accept(attendeeCheckInCounters);
        });
    }

    /**
     * Count the number of times each attendee has checked in
     * @param attendees the list of attendees to count check-ins for
     * @return an ArrayList of AttendeeCheckInCounter objects
     */
    public ArrayList<AttendeeCheckInCounter> countCheckIns(ArrayList<Attendee> attendees) {
        ArrayList<AttendeeCheckInCounter> attendeeCheckInCounters = new ArrayList<>();

        for (Attendee attendee : attendees) {
            int count = Collections.frequency(checkedInAttendees, attendee.getIdentifier());
            attendeeCheckInCounters.add(new AttendeeCheckInCounter(attendee, count));
        }
        return attendeeCheckInCounters;
    }
}
