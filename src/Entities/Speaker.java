package Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class Speaker extends User implements IndividualScheduleInterface, Serializable, EventParticipant {
    private final List<UUID> givenTalks;
    private final ArrayList<ArrayList<LocalDateTime>> schedule;

    public Speaker(String userId, String password) {
        super(userId, password);
        this.schedule = new ArrayList<>();
        this.givenTalks = new ArrayList<>();
    }

    /**
     * checks to see if a speaker
     * @return returns a boolean where it's true if self is a speaker
     */
    @Override
    public boolean isSpeaker() {return true;}

    /**
     * gets list of talks speaker gives
     * @return returns a list of UUIDs representing a list IDs for events they are talking at
     */
    public List<UUID> getGivenTalks() {
        return this.givenTalks;
    }

    /**
     * adds the event ID to the list of event ids the speaker is speaking at
     * @param eventId a UUID that represents an ID for an event that the speaker will speak at
     */
    public void addGivenTalks(UUID eventId) {
        this.givenTalks.add(eventId);
    }

    /**
     * removes the event ID to the list of event ids the speaker is speaking at
     * @param eventId a UUID that represents an ID for an event that the speaker spoke at
     */
    public void removeGivenTalks(UUID eventId) {
        this.givenTalks.remove(eventId);
    }

    /**
     * gets list of events speaker participates in
     * @return returns a list of UUIDs representing a list IDs for events speaker is talking at
     */
    public  List<UUID> getParticipantEvents() {
        return getGivenTalks();
    }

    /**
     * adds time to the schedule if space if free in that time slot
     * @param startTime A LocalDateTime variable that represents the start time of the event
     * @param endTime A LocalDateTime variable that represents the end time of the event
     * @return a Boolean return where it's true if the time is able to be added and false otherwise
     */
    public boolean addTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (this.isAvailable(startTime, endTime)){
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
            for (ArrayList<LocalDateTime> list : this.schedule) {
                if (startTime == list.get(0) && endTime == list.get(1)) {
                    time = list;
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
            for (ArrayList<LocalDateTime> list:this.schedule){
                if(startTime.isEqual(list.get(0)) && endTime.isEqual(list.get(1))){
                    return false;
                } else if (startTime.isAfter(list.get(0)) && startTime.isBefore(list.get(1))){
                    return false;
                } else if (endTime.isAfter(list.get(0))&&endTime.isBefore(list.get(1))){
                    return false;
                }
            }
        }
        return true;
    }

}
