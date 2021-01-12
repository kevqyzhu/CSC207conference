package Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Attendee extends User implements IndividualScheduleInterface, Serializable, EventParticipant {
    private final List<List<LocalDateTime>> schedule;
    private final List<String> pendingRequests;
    private final List<String> addressedRequests;

    public Attendee(String userId, String password) {

        super(userId, password);
        this.schedule = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.addressedRequests = new ArrayList<>();
    }

    public boolean isAttendee(){
        return true;
    }

    /**
     * gets list of events attendee participates in
     * @return returns a list of UUIDs representing a list IDs for events attendee is enrolled in
     */
    public List<UUID> getParticipantEvents() {
        return getEventIds();
    }

    /**
     * adds time to the schedule if space if free in that time slot
     * @param startTime A LocalDateTime variable that represents the start time of the event
     * @param endTime A LocalDateTime variable that represents the end time of the event
     * @return a Boolean return where it's true if the time is able to be added and false otherwise
     */
    public boolean addTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (this.isAvailable(startTime,endTime)){
            ArrayList<LocalDateTime> e = new ArrayList<>();
            e.add(startTime);
            e.add(endTime);
            this.schedule.add(e);
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * removes time from the schedule if the is something in that slot to be removed
     * @param startTime A LocalDateTime variable that represents the start time of the event
     * @param endTime A LocalDateTime variable that represents the end time of the event
     * @return a Boolean return where it's true if the time is able to be removed and false otherwise
     */
    public boolean removeTime(LocalDateTime startTime, LocalDateTime endTime){
        ArrayList<LocalDateTime> time = new ArrayList<>();
        boolean removed = false;
        if (!this.schedule.isEmpty()) {
        for (List<LocalDateTime> list:this.schedule) {
            if (startTime.equals(list.get(0)) && endTime.equals(list.get(1))) {
                time = (ArrayList<LocalDateTime>) list;
                removed = true;
            }
        }
        }
        if (removed){
            this.schedule.remove(time);
            return true;
        }
        return false;

    }
    /**
     * checks to see if the schedule is busy at this time
     * @param startTime A LocalDateTime variable that represents the start time of the event
     * @param endTime A LocalDateTime variable that represents the end time of the event
     * @return A boolean value where true is returned if the schedule is empty at this time and returns false otherwise
     */
    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime){
        if (!this.schedule.isEmpty()) {
            for (List<LocalDateTime> list : this.schedule) {
                if (startTime.isEqual(list.get(0)) && endTime.isEqual(list.get(1))) {
                    return false;
                } else if (startTime.isAfter(list.get(0)) && startTime.isBefore(list.get(1))) {
                    return false;
                } else if (endTime.isAfter(list.get(0)) && endTime.isBefore(list.get(1))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds a request to the Attendee's list of pending requests.
     * @param request The request to be added
     */
    public void addRequest(String request) {
        pendingRequests.add(request);
    }

    /**
     * Removes a request from the Attendee's list of pending requests and adds it to their list of addressed requests.
     * @param request The request to be moved
     */
    public void removeRequest(String request) {
        pendingRequests.remove(request);
        addressedRequests.add(request);
    }

    /**
     * Get the list of pending requests for the Attendee.
     * @return a list of pending requests
     */
    public List<String> getPendingRequests() {
        return pendingRequests;
    }

    /**
     * Get the list of addressed requests for the Attendee.
     * @return a list of addressed requests
     */
    public List<String> getAddressedRequests() {
        return addressedRequests;
    }
}
