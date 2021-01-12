package Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event implements Serializable {
    private final UUID eventId;
    private final String name;
    private UUID roomId;
    private final ArrayList<String> speakerIds;
    private final List<String> attendeeIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int capacity;
    private final boolean isVipEvent;

    /**
     *
     * @param name A String representing the name of the event
     * @param startTime A LocalDateTime variable representing the start time and Date of the event
     * @param endTime A LocalDateTime variable representing the end time and date of the event
     * @param speakerIDs An ArrayList<String> representing the login IDs of the speakers who are speaking at the event
     */
    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, ArrayList<String> speakerIDs,
                         int capacity, boolean isVipEvent) {
        this.eventId = UUID.randomUUID();
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.speakerIds = speakerIDs;
        this.attendeeIds = new ArrayList<>();
        this.capacity = capacity;
        this.isVipEvent = isVipEvent;
    }



    /**
     * gets name of event
     * @return returns the Name of the event as a String
     */
    public String getName() { return this.name; }

    /**
     * gets whether this event is a VIP event
     * @return returns whether this event is a VIP event
     */
    public boolean getIsVipEvent() { return this.isVipEvent; }

    /**
     * gets id of event
     * @return returns the eventID as a UUID
     */
    public UUID getEventId() {
        return this.eventId;
    }

    /**
     * gets the room id of the event
     * @return returns the room ID for this event as a UUID
     */
    public UUID getRoomId() {
        return this.roomId;
    }

    /**
     * sets the room id of the event
     * @param roomId a UUID representing the unique ID for a room that you want this event to be scheduled at
     */
    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    /**
     * gets the start time of the event
     * @return returns a LocalDateTime object representing the start time of the event
     */
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * sets the start time of the event
     * @param startTime a LocalDateTime variable representing the desired start time of the event
     */
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    /**
     * gets the end time of the event
     * @return returns a LocalDateTime object representing the end time of the event
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * sets the end time of the event
     * @param endTime a LocalDateTime variable representing the desired end time of the event
     */
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    /**
     * gets the speaker ids of the speakers speaking at the event
     * @return returns an ArrayList<String> that represents the IDs of the speakers for this event
     */
    public ArrayList<String> getSpeakerIds() {
        return this.speakerIds;
    }

    /**
     * removes a speaker who was speaking at the event
     * @param sID a String representing the Speaker ID
     */
    public void removeSpeakerId(String sID) {
        this.speakerIds.remove(sID);
    }

    /**
     * gets a list of attendee ids for people attending the event
     * @return returns a list of Strings that represents the Usernames of all the Attendees for this event
     */
    public List<String> getAttendeeIds() {
        return attendeeIds;
    }

    /**
     * adds an attendee to the list of attendees attending the event
     * @param aID a String representing the Attendee Login
     */
    public void addAttendeeIds(String aID){ this.attendeeIds.add(aID);}

    /**
     * removes an attendee who was attending the event
     * @param aID a String representing the Attendee Login
     */
    public void removeAttendeeId(String aID){this.attendeeIds.remove(aID);}
    /**
     * provides a string to display name, times and speakers
     * @return returns the name of the event with start and end time and the speaker speaking at the event all in String
     * form
     */
    public String toString () {
        String s = "";
        s += "Event: ";
        s += this.name+", ";
        s += "Speaker: " + this.speakerIds.toString().substring(1, this.speakerIds.toString().length()-1) + " ";
        String startTime = this.getStartTime().toString().split("T", 2)[1];
        String endTime = this.getEndTime().toString().split("T", 2)[1];
        String startMonth = this.getStartTime().getMonth().toString().charAt(0) + this.getStartTime().getMonth().toString().substring(1).toLowerCase();

        s += "(" + startMonth + " " + this.startTime.getDayOfMonth() + " " + this.startTime.getYear() + " ";
        s += "from " + startTime + "-"+ endTime + ")";
        return s;
    }

    /**
     * returns a string indicating the type of event this is.
     * @return String
     */
    public String getEventType() {
        if (this.speakerIds.size()==0) {
            return "party";
        }
        else if (this.speakerIds.size()==1) {
            return "talk";
        }
        else { // multiple speakers => panel
            return "panel";
        }
    }

    /**
     * sets capacity of event
     * @param capacity capacity of event
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     *
     * @return integer with capacity of event
     */
    public int getCapacity() {
        return capacity;
    }
}
