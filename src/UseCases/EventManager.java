package UseCases;//import javafx.util.StringConverter;
//import javafx.util.converter.DateTimeStringConverter;
//import javafx.util.converter.LocalDateTimeStringConverter;

import Entities.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class EventManager implements Serializable {
    /*\
    * Creates and stores all instances of events, rooms.
    * Creates a list to store all users that are scheduled in at least 1 event
    *
    * Stores a schedule of all rooms and events
    * */

    private final HashMap<Room,ArrayList<Event>> schedule;
    //map where keys are rooms, values are lists of events

    public EventManager(){
        this.schedule = new HashMap<>();
    }

    /**
     * gets the schedule of room and events
     * @return returns a HashMap where the keys are Room objects and the values are ArrayLists of Event objects
     */
    public HashMap<Room,ArrayList<Event>> getSchedule() { return new HashMap<>(schedule); }

    /**
     * Simply creates an event with the passed in information, and adds it to the schedule map. The room's and
     *         speaker's schedule must be adjusted appropriately by controller class(es). A room will *not* be auto created
     *         if it doesn't exist yet.
     *         returns the event object.
     * @param name a String that represents the name of the event
     * @param roomID a UUID that is unique to a specific room object
     * @param speakers an arraylist of Speaker objects that represents the speakers of the event
     * @param t an array list of 2 LocalDateTime variables that represent the start and end time of the event
     * @return an Event object representing the event that was just created
     */
    public Event createEvent(String name, UUID roomID, ArrayList<Speaker> speakers, ArrayList<LocalDateTime> t, int size, boolean isVipEvent){
        ArrayList<String> speakerIds = new ArrayList<>();
        for (Speaker speaker: speakers) {
            speakerIds.add(speaker.getUserId());
        }
        Event event = new Event(name,t.get(0),t.get(1),speakerIds,size, isVipEvent);
        Room room = this.getRoom(roomID);
        event.setRoomId(roomID);
        if (this.schedule.containsKey(room) && !this.schedule.get(room).contains(event)){
            this.schedule.get(room).add(event);
        }
        return event;
    }

    /*private boolean isRoom(UUID roomID){
        Set<Room> list = schedule.keySet();
        if (!list.isEmpty()) {
            for (Room r : list) {
                if (r.getId().equals(roomID)) {
                    return true;
                }
            }
        }
        return false;
    }*/

    /**
     * Returns the room with ID value of id. Returns null if this room doesn't exist.
     * @param id a UUID that is unique to a specific room
     * @return Returns a Room object
     */
    public Room getRoom(UUID id){
        Room room = null;
        Set<Room> list = schedule.keySet();
        if (!list.isEmpty()) {
            for (Room r : list) {
                if (r.getId().equals(id)) {
                    room = r;
                }
            }
        }
        return room;
    }

    /**
     * gets a room based on it's name
     * @param name a String that represents the name of the room
     * @return Returns a Room object
     */
    public Room getRoom(String name) {
        Room room = null;
        Set<Room> list = schedule.keySet();
        if (!list.isEmpty()) {
            for (Room r : list) {
                if (r.getName().equals(name)) {
                    room = r;
                }
            }
        }
        return room;
    }

    /**
     * gets a room based on it's index in getAllRooms()
     * @param roomIndex an int that represents the index of the room in getAllRooms()
     * @return Returns a Room object
     */
    public Room getRoomByIndex(int roomIndex) {
        if (roomIndex < this.getAllRooms().size() && roomIndex >=0) {
            return this.getAllRooms().get(roomIndex);
        }
        return null;
    }

    /**
     * gets an event based on it's index in getAllEvents()
     * @param eventIndex an int that represents the index of the event in getAllEvents()
     * @return Returns a Room object
     */
    public Event getEventByIndex(int eventIndex) {
        return this.getAllEvents().get(eventIndex);
    }

    /**
     * gets an event based on its name in getAllEventNames()
     * @param eventName a String that is the name of the event
     * @return Returns an Event object
     */
    public Event getEventByName(String eventName) {
        return getEventByIndex(getAllEventNames().indexOf(eventName));
    }

    /**
     * gets an event based on its string in getAllEventStrings()
     * @param eventString a String that gives all the information of the event
     * @return Returns an Event object
     */
    public Event getEventByString(String eventString) {
        return getEventByIndex(getAllEventStrings().indexOf(eventString));
    }

//    /**
//     * Creates and stores a room object in the schedule map. This method is called by Controllers.EventScheduler.
//     * @param name a String that represents the name of the room
//     * @return Returns a Room object
//     */
//    public Room createRoom(String name,int size){
//        Room room = new Room(name, size);
//        ArrayList<Event> list = new ArrayList<>();
//        this.schedule.put(room,list);
//        return room;
//    }

    /**
     * Creates and stores a room object in the schedule map. This method is called by Controllers.EventScheduler.
     * @param name a String that represents the name of the room
     */
    public boolean createRoom(String name, int size) {
        Room room = new Room(name, size);
        if (getRoom(name) == null) {
            ArrayList<Event> list = new ArrayList<>();
            this.schedule.put(room,list);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Iterates through schedule looking for event e, if its found, it will remove it. Note that this method
     *         will not update attendees or speakers schedule, or the event ids for any attendees. This must be handled by
     *         the controller that calls this method.
     *         This event will *not* clear the list of attendees either, in case the event is to be reassigned to a new
     *         room.
     * @param event an Event object that you wish to be removed from the schedule
     */
    public void removeEvent(Event event){
        Room room = this.getRoom(event.getRoomId());
        if (this.schedule.containsKey(room)) {
            this.schedule.get(room).remove(event);
        }
    }

    /**
     * This will simply add a.id to the list of e's attendee ids, and add e.id to a's list of event ids. Schedule
     *         changing (i.e. updating attendee schedule and checking to make sure we aren't overfilling the room will be
     *         done in Controllers.EventSignup). Returns true if the id is successfully added, false if the id was already in the
     *         list of attendees for the event.
     * @param a an Attendee object representing an Attendee that wishes to join the event
     * @param e an Event object that represents the event that the user wishes to sign up to
     */
    public void addUser(Attendee a, Event e){
        e.addAttendeeIds(a.getUserId());
        a.addEventId(e.getEventId());

    }


    /**
     * Remove a.id from the list of attendees ids, remove the event id
     *         from the user's EventId's.
     *         Schedule changes will happen in Controllers.EventSignup.
     * @param a an Attendee object representing an Attendee that wishes to not attend the event
     * @param e an Event object that represents the event that the user wishes not attend
     */
    public void removeUser(Attendee a, Event e){
        e.removeAttendeeId(a.getUserId());
        a.removeEventId(e.getEventId());
    }

    /**
     * Remove a.id from the list of attendees ids, remove the event id
     *         from the user's EventId's.
     *         Schedule changes will happen in Controllers.EventSignup.
     * @param s a Speaker object representing an Speaker that wishes to not attend the event
     * @param e an Event object that represents the event that the user wishes not attend
     */
    public void removeSpeaker(Speaker s, Event e){
        e.removeSpeakerId(s.getUserId());
        s.removeGivenTalks(e.getEventId());
    }

    /**
     * Iterates through all rooms until it finds an event with this id, and return the Entities.Event object.
     * @param id a UUID that is unique to a specific event
     * @return returns the Event object that matches the inputted UUID
     */
    public Event getEvent(UUID id){
        if (!schedule.keySet().isEmpty()) {
            for (Room r : schedule.keySet()) {
                for (Event event1 : schedule.get(r)) {
                    Event event = (Event) event1;
                    if (id.equals(event.getEventId())) {
                        return event;
                    }
                }
            }
        }
        return null; // if event id not found in the schedule
    }

    /**
     * returns a list of all rooms in the schedule
     * @return returns a list of all Room objects currently stored within the Event Manager
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        if (!schedule.keySet().isEmpty()) {
            rooms.addAll(schedule.keySet());
        }
        return rooms;
    }

    /**
     * returns a list of all events in the schedule
     * @return returns a list of all Event objects currently stored within the Event Manager
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        if (!schedule.keySet().isEmpty()) {
            for (Room room : schedule.keySet()) {
                events.addAll(schedule.get(room));
            }
        }
        return events;
    }

    /**
     * Returns a list of VIP events in a list of events
     * @return List<Event> a list of VIP events
     */
    public List<Event> getVipEvents(List<Event> events) {
        List<Event> vipEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getIsVipEvent()) {vipEvents.add(event);}
        }
        return vipEvents;
    }

    /**
     * @return List<String> a list of all event names stored in eventManager.
     */
    public List<String> getAllEventNames() {
        List<String> allEventNames = new ArrayList<>();
        for (Event event : getAllEvents()) {
            allEventNames.add(event.getName());
        }
        return allEventNames;
    }

    /**
     * @return List<String> a list of all event strings stored in eventManager.
     */
    public List<String> getAllEventStrings() {
        List<String> allEventStrings = new ArrayList<>();
        for (Event event : getAllEvents()) {
            allEventStrings.add(event.toString());
        }
        return allEventStrings;
    }

    /**
     * @param events a list of events
     * @return List<String> a list of all event names stored in List of events
     */
    public List<String> getEventNames(List<Event> events) {
        List<String> eventNames = new ArrayList<>();
        for (Event event : events) {
            eventNames.add(event.getName());
        }
        return eventNames;
    }

    /**
     * returns a list of all possible dates to schedule events in
     * @return returns a list of all possible dates to schedule events on
     */
    public List<LocalDate> getConferenceDates() {
        List<LocalDate> dates = new ArrayList<>();
        List<Event> events = getAllEvents();
        if (!events.isEmpty()) {
            for (Event event : events) {
                LocalDate date = event.getStartTime().toLocalDate();
                if (!dates.contains(date)) {
                    dates.add(date);
                }
            }
        }
        Collections.sort(dates);
        return dates;
    }

    /**
     * returns list of events based on the speaker speaking at him
     * @param speaker a Speaker object representing the speaker for which you want to see all events they are speaking
     *                at
     * @return returns a list of events that the inputted speaker is speaking at
     */
    public List<Event> getEventsBySpeaker(Speaker speaker) {
        List<Event> events = new ArrayList<>();
        if (!speaker.getGivenTalks().isEmpty()) {
            for (UUID eventId : speaker.getGivenTalks()) {
                events.add(getEvent(eventId));
            }
        }
        return events;
    }

    /**
     * returns if an attendee can be added or not
     * @param eventId a UUID that is unique to a specific event
     * @return returns true if another attendee can be added to the inputted event, false otherwise
     */
    public boolean canAddAttendee(UUID eventId) {
        Event event = this.getEvent(eventId);
        return event.getAttendeeIds().size() < this.getRoom(event.getRoomId()).getCapacity() &&
                event.getAttendeeIds().size() < event.getCapacity();
    }

    public void changeEventSize(Event event, int newSize){
        event.setCapacity(newSize);
    }

}
