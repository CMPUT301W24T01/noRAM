/*
This file is used to create an event object. It contains all the attributes of an event and the methods to get and set them.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import androidx.annotation.Nullable;

import com.example.noram.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private String checkInQRID;
    private String promoQRID;
    private boolean trackLocation;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String organizerId;
    private List<String> checkedInAttendees;
    private List<String> signedUpAttendees;
    private Long signUpLimit;
    private List<GeoPoint> checkedInAttendeesLocations;
    private Long lastMilestone;
    private List<Notification> notifications;
    private boolean locationIsRealLocation;
    private GeoPoint locationCoordinates;

    /**
     * Default constructor for Event. Often used to create an event shell where we can then populate
     * from a document, or for creating events when we only actually care about the ID, for navigation
     * purposes.
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
     * @param organizerId the id of the organizer who created the event
     * @param signUpLimit number of signups for event allowed (-1 for no limit)
     * @param lastMilestone the last milestone that was achieved
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
            String organizerId,
            Long signUpLimit,
            Long lastMilestone,
            GeoPoint locationCoords) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.trackLocation = trackLocation;
        this.checkedInAttendees = new ArrayList<>();
        this.organizerId = organizerId;
        this.signedUpAttendees = new ArrayList<>();
        this.signUpLimit = signUpLimit;
        this.checkedInAttendeesLocations = new ArrayList<>();
        this.lastMilestone = lastMilestone;
        this.notifications = new ArrayList<>();
        locationCoordinates = locationCoords;
        locationIsRealLocation = locationCoords != null;
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
     * @param checkInQRID ID of QR code used to check user in to event
     * @param promoQRID id of QR code used to promote the event
     * @param trackLocation is location tracking of check-ins enabled
     * @param checkedInAttendees list of checked in attendees
     * @param organizerId the id of the organizer who created the event
     * @param signedUpAttendees list of signed up attendees
     * @param signUpLimit number of signups for event allowed (-1 for no limit)
     * @param checkedInAttendeesLocations list of attendee locations
     * @param lastMilestone the last milestone that was achieved
     * @param notifications list of notifications
     */
    public Event(
            String id,
            String name,
            String location,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String details,
            ArrayList<Integer> milestones,
            String checkInQRID,
            String promoQRID,
            boolean trackLocation,
            List<String> checkedInAttendees,
            String organizerId,
            List<String> signedUpAttendees,
            Long signUpLimit,
            List<GeoPoint> checkedInAttendeesLocations,
            Long lastMilestone,
            List<Notification> notifications,
            GeoPoint locationCoordinates) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.milestones = milestones;
        this.checkInQRID = checkInQRID;
        this.promoQRID = promoQRID;
        this.trackLocation = trackLocation;
        this.checkedInAttendees = checkedInAttendees;
        this.organizerId = organizerId;
        this.signedUpAttendees = signedUpAttendees;
        this.signUpLimit = signUpLimit;
        this.checkedInAttendeesLocations = checkedInAttendeesLocations;
        this.lastMilestone = lastMilestone;
        this.notifications = notifications;
        this.locationCoordinates = locationCoordinates;
        locationIsRealLocation = locationCoordinates != null;
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

    /**
     * Returns the last milestone that was achieved
     * @return lastMilestone attribute
     */
    public Long getLastMilestone() {
        return lastMilestone;
    }

    /**
     * Returns the number of checked-in attendees
     * @return size of checkedInAttendees ArrayList
     */
    public List<Notification> getNotifications() {
        return notifications;
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
     * Get the list of checked in attendees locations
     * @return list of attendee locations
     */
    public List<GeoPoint> getCheckedInAttendeesLocations() {
        return checkedInAttendeesLocations;
    }

    /**
     * Set the list of checked in attendees locations
     * @param checkedInAttendeesLocations new list of checked in attendees locations
     */
    public void setCheckedInAttendeesLocations(List<GeoPoint> checkedInAttendeesLocations) {
        this.checkedInAttendeesLocations  = checkedInAttendeesLocations;
    }

    /**
     * Get the ID of the organizer associated with the event
     * @return string id.
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Set the organizer ID, the organizer who created the event
     * @param organizerId new organizer ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Get the id for the checkin QR code for this event
     * @return string id
     */
    public String getCheckInQRID() {
        return checkInQRID;
    }

    /**
     * Set the id for the checkin QR code for this event
     * @param checkInQRID new id string
     */
    public void setCheckInQRID(String checkInQRID) {
        this.checkInQRID = checkInQRID;
    }

    /**
     * Get the id for the promotional qr code for this event
     * @return string id
     */
    public String getPromoQRID() {
        return promoQRID;
    }

    /**
     * Set the id for the promotional qr code for this event
     * @param promoQRID new id string
     */
    public void setPromoQRID(String promoQRID) {
        this.promoQRID = promoQRID;
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

    /**
     * Sets the last milestone that was achieved
     * @param lastMilestone new last milestone
     */
    public void setLastMilestone(Long lastMilestone) {
        this.lastMilestone = lastMilestone;
    }

    /**
     * Sets the list of notifications
     * @param notifications new list of notifications
     */
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Returns whether the event's location is a real location that has coordinates
     * @return true if it has coordinates; false otherwise
     */
    public boolean getLocationIsRealLocation() {
        return locationIsRealLocation;
    }

    /**
     * Set whether the location is a real location with coordinates
     * @param locationIsRealLocation new value
     */
    public void setLocationIsRealLocation(boolean locationIsRealLocation) {
        this.locationIsRealLocation = locationIsRealLocation;
    }

    /**
     * Get the location GeoPoint coordinates
     * @return null if locationIsRealLocation is false, otherwise the coordinates
     */
    public GeoPoint getLocationCoordinates() {
        return locationCoordinates;
    }

    /**
     * Set the location geopoint coordinates
     * @param locationCoordinates new value
     */
    public void setLocationCoordinates(GeoPoint locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
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
        data.put("checkInQRID", checkInQRID);
        data.put("promoQRID", promoQRID);
        data.put("trackLocation", trackLocation);
        data.put("checkedInAttendees", checkedInAttendees);
        data.put("checkedInAttendeesLocations", checkedInAttendeesLocations);
        data.put("organizerID", organizerId);
        data.put("signedUpAttendees", signedUpAttendees);
        data.put("signUpLimit", signUpLimit);
        data.put("lastMilestone", lastMilestone);
        data.put("notifications", notifications);
        data.put("locationIsRealLocation", locationIsRealLocation);
        data.put("locationCoordinates", locationCoordinates);
        MainActivity.db.getEventsRef().document(id).set(data);
    }

    /**
     * Updates the event given a database 'event' document
     * @param doc The database document containing the fields used to update the event instance
     */
    public void updateWithDocument(DocumentSnapshot doc) {
        this.setId(doc.getId());
        this.setName(doc.getString("name"));
        this.setDetails(doc.getString("details"));
        this.setLocation(doc.getString("location"));
        this.setTrackLocation(Boolean.TRUE.equals(doc.getBoolean("trackLocation")));
        this.setStartTime(LocalDateTime.parse(doc.getString("startTime"), formatter));
        this.setEndTime(LocalDateTime.parse(doc.getString("endTime"), formatter));
        this.setCheckedInAttendees((List<String>) doc.get("checkedInAttendees"));
        //grab checkedInAttendeesLocations from db
        List<HashMap<String, Object>> geoData = (List<HashMap<String, Object>>) doc.get("checkedInAttendeesLocations");
        //then use parseGeopointListFromDatabase() before setting the event attribute
        this.setCheckedInAttendeesLocations(parseGeopointListFromDatabase(geoData));
        this.setMilestones((ArrayList<Integer>) doc.get("milestones"));
        this.setPromoQRID(doc.getString("promoQRID"));
        this.setCheckInQRID(doc.getString("checkInQRID"));
        this.setOrganizerId(doc.getString("organizerID"));
        this.setCheckedInAttendees((List<String>) doc.get("checkedInAttendees"));
        this.setSignedUpAttendees((List<String>) doc.get("signedUpAttendees"));
        this.setSignUpLimit(doc.getLong("signUpLimit"));
        this.setLastMilestone(doc.getLong("lastMilestone"));

        this.setLocationIsRealLocation(doc.getBoolean("locationIsRealLocation"));
        if (locationIsRealLocation) {
            HashMap<String, Object> data = (HashMap<String, Object>) doc.get("locationCoordinates");
            double latitude = (double) data.get("latitude");
            double longitude = (double) data.get("longitude");
            locationCoordinates = new GeoPoint(latitude, longitude);
        } else {
            locationCoordinates = null;
        }

        this.setNotifications(new ArrayList<>());
        if (doc.get("notifications") != null) {
            for (HashMap<String, String> notification : (List<HashMap<String, String>>) doc.get("notifications")) {
                this.addNotification(new Notification(notification.get("title"), notification.get("content")));
            }
        }
    }

    /**
     * Adds a notification to the event
     * @param notification the notification to add
     */
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    /**
     * Parses the list of hashmaps firestore provides for attendee locations into a list of GeoPoint
     * objects
     * @author Cole
     * @param databaseInfo data from firestore
     * @return parsed list of geopoints
     */
    private List<GeoPoint> parseGeopointListFromDatabase(List<HashMap<String,Object>> databaseInfo) {
        ArrayList<GeoPoint> geopointList = new ArrayList<>();
        //on first download from db, this list is null?
        if (databaseInfo != null){
            for (HashMap<String, Object> entry : databaseInfo) {
                double latitude = (double) entry.get("latitude");
                double longitude = (double) entry.get("longitude");
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                geopointList.add(geoPoint);
            }
        }
        return geopointList;
    }

    /**
     * Adds string representation of attendee into signedUpAttendees list
     * @param attendee string representation of attendee
     */
    public void addSignedUpAttendee(String attendee) {
        signedUpAttendees.add(attendee);
    }

    /**
     * Remove the string representation of an attendee from the signedUpAttendees list
     * @param attendee string representation of the attendee being removed
     */
    public void removeSignedUpAttendee(String attendee){ signedUpAttendees.remove(attendee);}

    /**
     * Get the list of checked in attendees and the number of times they have checked in to provide to the callback
     * @param callback the callback to provide the list of attendees and their check-in counts to
     */
    public void getCheckedInAttendeesAndCounts(Consumer<ArrayList<AttendeeCheckInCounter>> callback) {
        ArrayList<Attendee> checkedInAttendeeObjects = new ArrayList<>();
        // If no attendees have checked in, return an empty list
        if (checkedInAttendees.isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }

        // Get the attendee objects for each checked in attendee
        MainActivity.db.getAttendeeRef().whereIn("identifier", checkedInAttendees).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                Map<String, Object> data = document.getData();
                Attendee attendee = new Attendee((String) data.get("identifier"));
                attendee.updateWithMap(data);
                checkedInAttendeeObjects.add(attendee);
            }

            // Count the number of times each attendee has checked in and return the list
            ArrayList<AttendeeCheckInCounter> attendeeCheckInCounters = countCheckIns(checkedInAttendeeObjects);
            callback.accept(attendeeCheckInCounters);
        });
    }

    /**
     * Get the list of signed up attendees
     * @param callback the callback to provide the list of attendees to
     */
    public void getSignedUpAttendeeObjects(Consumer<ArrayList<Attendee>> callback) {
        ArrayList<Attendee> signedUpAttendeeObjects = new ArrayList<>();
        // If no attendees have signed in, return an empty list
        if (signedUpAttendees.isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }

        // Get the attendee objects for each signed-up attendee
        MainActivity.db.getAttendeeRef().whereIn("identifier", signedUpAttendees).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
               Map<String, Object> data = document.getData();
               Attendee attendee = new Attendee((String) data.get("identifier"));
               attendee.updateWithMap(data);
               signedUpAttendeeObjects.add(attendee);
           }
            callback.accept(signedUpAttendeeObjects);
        });

    }

    /**
     * Count the number of times each attendee has checked in
     * @param attendees the list of attendees to count check-ins for
     * @return an ArrayList of AttendeeCheckInCounter objects
     */
    public ArrayList<AttendeeCheckInCounter> countCheckIns(ArrayList<Attendee> attendees) {
        ArrayList<AttendeeCheckInCounter> attendeeCheckInCounters = new ArrayList<>();

        // Count the number of times each attendee has checked in
        for (Attendee attendee : attendees) {
            int count = Collections.frequency(checkedInAttendees, attendee.getIdentifier());
            attendeeCheckInCounters.add(new AttendeeCheckInCounter(attendee, count));
        }
        return attendeeCheckInCounters;
    }

    /**
     * Get an arraylist of pairs of milestones and the current number of attendees
     * @return ArrayList of pairs of milestone and number of attendees
     */
    public ArrayList<Milestone> getMilestoneCounts() {
        HashSet<String> uniqueAttendees = new HashSet<>(checkedInAttendees);
        ArrayList<String> uniqueAttendeesList = new ArrayList<>(uniqueAttendees);

        // Get the total number of unique attendees
        int total = uniqueAttendeesList.size();

        // Create a set of milestone objects to remove duplicates automatically
        Set<Milestone> milestoneCounts = new HashSet<>();
        for (int i = 0; i < milestones.size(); i++) {
            Integer milestone = Integer.valueOf(String.valueOf(milestones.get(i)));
            milestoneCounts.add(new Milestone(milestone, total));
        }
        // Convert the set to a list and sort it
        ArrayList<Milestone> milestoneCountsList = new ArrayList<>(milestoneCounts);
        milestoneCountsList.sort(Milestone::compareTo);

        return milestoneCountsList;
    }

    /**
     * Get the number of unique attendees that have checked in to the event
     * @return the number of attendees that have checked in
     */
    public int getUniqueAttendeeCount() {
        HashSet<String> uniqueAttendees = new HashSet<>(checkedInAttendees);
        return uniqueAttendees.size();
    }

    /**
     * Check if the event is happening right now
     * @return Returns true if the event is happening right now, false otherwise
     */
    public boolean isHappeningNow(){
        LocalDateTime currentTime = LocalDateTime.now();
        return this.getStartTime().isBefore(currentTime) && this.getEndTime().isAfter(currentTime);
    }

    /**
     * Check if the event already happened (if current time is after event's endtime)
     * @return True if the event already happened, false otherwise
     */
    public boolean hasHappened(){
        return endTime.isBefore(LocalDateTime.now());
    }
}
