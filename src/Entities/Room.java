package Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Room implements IndividualScheduleInterface, Serializable {
    private final UUID id;
    private final ArrayList<ArrayList<LocalDateTime>> schedule;
    private final Integer capacity;
    private final String name;

    /**
     *
     * @param name A string representing the name of the room
     */
    public Room(String name, int capacity) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.schedule = new ArrayList<>();
        this.capacity = capacity;
    }

    /**
     * gets id
     * @return returns the UUID representing the ID of the room
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * gets schedule
     * @return returns an Array list of Array lists of LocalDateTime variables to represent start and end times of
     * events happening in this room
     */
    public ArrayList<ArrayList<LocalDateTime>> getSchedule() {
        return this.schedule;
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
        ArrayList<LocalDateTime> remove = new ArrayList<>();
        boolean removed = false;
        if (!this.schedule.isEmpty()) {
            for (ArrayList<LocalDateTime> list : this.schedule) {
                if (startTime == list.get(0) && endTime == list.get(1)) {
                    remove = list;
                    removed = true;
                }
            }
        }
        if (removed){
            this.schedule.remove(remove);
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
            for (ArrayList<LocalDateTime> list : this.schedule) {
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
     * gives the capacity of the room
     * @return Returns an Integer that represents the Capacity of a room
     */
    public Integer getCapacity(){
        return this.capacity;
    }

    /**
     * gets name of room
     * @return returns a String representing the name of the room
     */
    public String getName(){return this.name;}
}
