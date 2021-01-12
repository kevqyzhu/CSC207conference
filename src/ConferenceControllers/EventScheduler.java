package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.*;
import UseCases.EventManager;
import UseCases.UserManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EventScheduler extends ConferenceController {
    EventManager eventManager;
    UserManager userManager;

    public EventScheduler(EventManager eventManager, UserManager userManager, MainMenu mainMenu) {
        super(mainMenu);
        this.eventManager = eventManager;
        this.userManager = userManager;
    }

    /**
     * Calls getRoom from UseCases.EventManager.
     *         Check if the room provided is available during the times provided, and if the speakers are available
     *         (it gets the speaker objects from the usermanager based on the IDs passed in) then calls createEvent.
     *         This method also needs to update the speakers' and the room's schedules.
     * @param name String Name of event
     * @param startTime LocalDateTime the time the event is set to start
     * @param endTime LocalDateTime the time the event is set to end
     * @param speakerIds ArrayList<String> a list of usernames unique to the speakers
     * @param roomId UUID that uniquely identifies a room.
     * @return Event the event created
     */
    public boolean createEvent(String name, LocalDateTime startTime, LocalDateTime endTime, List<String> speakerIds,
                             UUID roomId, int size, boolean isVipEvent) {
        Room room = this.eventManager.getRoom(roomId);
        ArrayList<Speaker> speakers = new ArrayList<>();

        if (room==null || room.getCapacity() < size || startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            return false;
        }
        for (String speakerId: speakerIds) {
            speakers.add(this.userManager.getSpeaker(speakerId));
        }
        ArrayList<LocalDateTime> dates = new ArrayList<>();
        dates.add(startTime);
        dates.add(endTime);
        if (room.isAvailable(startTime, endTime)) {
            for (Speaker speaker: speakers) {
                if (!speaker.isAvailable(startTime, endTime)) {
                    return false;
                }
            }
            Event event = this.eventManager.createEvent(name, roomId, speakers, dates, size, isVipEvent);
            for (Speaker speaker: speakers) {
                speaker.addTime(startTime, endTime);
                speaker.addGivenTalks(event.getEventId());
                speaker.getGivenTalks().removeIf(eventId -> this.eventManager.getEvent(eventId) == null);
            }

            room.addTime(startTime, endTime);
            event.setRoomId(room.getId());
            return true;
        }
        return false;
    }

//    /**
//     * Creates an event for the specified room index.
//     * @param name String Name of event
//     * @param startTime LocalDateTime the time the event is set to start
//     * @param endTime LocalDateTime the time the event is set to end
//     * @param speakerIds ArrayList<String> a list of usernames unique to the speakers
//     * @param roomIndex int an int representing the index of where a room is stored.
//     */
//    public void createEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
//                            ArrayList<String> speakerIds, int roomIndex, int size) {
//
//        Room room =  eventManager.getAllRooms().get(roomIndex);
//        createEvent(name, startTime, endTime, speakerIds, room.getId(), size);
//    }


    /**
     * Get event object first from UseCases.EventManager. Then, get the list of attendees and speakers from that, then
     *         update their schedules appropriately. Then call the removeEvent method in UseCases.EventManager. Also this will update
     *         The room schedule
     * @param eventID UUID that uniquely identifies an event.
     */
    public void removeEvent(UUID eventID) {
        Event event = this.eventManager.getEvent(eventID);
        if (!(event ==null)) {
            ArrayList<Attendee> attendees = new ArrayList<>();
            ArrayList<Speaker> speakers = new ArrayList<>();

            for (String speakerId: event.getSpeakerIds()) {
                speakers.add(userManager.getSpeaker(speakerId));
            }

            if (!event.getAttendeeIds().isEmpty()) {
                for (String attendeeId : event.getAttendeeIds()) {
                    attendees.add(this.userManager.getAttendee(attendeeId));
                }
            }
            if (!attendees.isEmpty()) {
                for (Attendee attendee : attendees) {
                    attendee.removeTime(event.getStartTime(), event.getEndTime());
                    eventManager.removeUser(attendee, event);
                }
            }
            for (Speaker speaker: speakers) {
                speaker.getGivenTalks().remove(eventID);
                speaker.removeTime(event.getStartTime(), event.getEndTime());
            }

            Room room = this.eventManager.getRoom(event.getRoomId());
            room.removeTime(event.getStartTime(),event.getEndTime());
            eventManager.removeEvent(event);
        }
    }

    /**
     * Delete any empty events in conference
     * if no events are empty returns to main menu
     * if there are empty events, prints them and asks user to input index
     * parses input and calls removeEvent to delete event
     * returns to main menu
     * if any exceptions occur, restarts process
     */
    public void handleDeleteEmptyEvent()  {
        try{
            List<Event> events = getAllEmptyEvent();
            if(events.size() == 0){
                presenter.print("There are no empty events.");
                mainMenu.handleMainMenu();
            }
            presenter.print("Here are the empty events in this conference:");
            presenter.printSchedule(events);
            presenter.printIndexRequest();
            int index = checkInt(parser(), events.size());
            removeEvent(events.get(index).getEventId());
            mainMenu.handleMainMenu();
        } catch (Exception e){
            handleDeleteEmptyEvent();
        }
    }

    /**
     * helper method for handleDeleteEmpty event
     * @return list of empty events
     */
    public List<Event> getAllEmptyEvent(){
        List <Event> events = eventManager.getAllEvents();
        List <Event> events1 = new ArrayList<>();
        for(Event event : events){
            if (event.getAttendeeIds().isEmpty()){
                events1.add(event);
            }
        }
        return events1;
    }

    /**
     * Gets the room and event objects, removes the old start/end time of the event from the room's schedule, and
     *         tries adding the new start/end times. If it can do this without issue, then it will call removeEvent() in
     *         Controllers.EventScheduler to remove the old event, and createEvent to create the new one with the specified information.
     *         If it can't add the new times without error, it will add the old times in again and return null.
     *
     *         *NOTE: This method does NOT transfer attendees from the original event to the rescheduled one, even if
     *         they are able to attend the new scheduled time
     * @param eventId UUID uniquely identifies an event.
     * @param roomId UUID uniquely identifies a room.
     * @param newStartTime LocalDateTime the new start time for the event.
     * @param newEndTime LocalDateTime the new end time for the event.
     */
    public boolean rescheduleEvent(UUID eventId, UUID roomId, LocalDateTime newStartTime, LocalDateTime newEndTime, int size,
                                   boolean isVipEvent) {
        Room room = this.eventManager.getRoom(roomId);
        Event event = this.eventManager.getEvent(eventId);
        LocalDateTime oldStartTime = event.getStartTime();
        LocalDateTime oldEndTime = event.getEndTime();

        // if the event is to be rescheduled to a new time in the same room
        if (roomId == event.getRoomId()) {
            room.removeTime(event.getStartTime(), event.getEndTime());
            if (room.isAvailable(newStartTime, newEndTime)) {
                this.removeEvent(event.getEventId());
                return this.createEvent(event.getName(), newStartTime, newEndTime, event.getSpeakerIds(), roomId, size, isVipEvent);

            } else {
                room.addTime(oldStartTime, oldEndTime);
            }
        }
        // if the event is to be rescheduled to a new time in a different room
        else {
            if (room.isAvailable(newStartTime, newEndTime)) {
                this.removeEvent(event.getEventId());
                return this.createEvent(event.getName(), newStartTime, newEndTime, event.getSpeakerIds(), roomId, size, isVipEvent);
            }
        }
        return false;
    }



    /**
     * @return List<String> a list of all room names
     */
    public List<String> getAllRoomNames() {
        List<String> allRoomNames = new ArrayList<>();
        for (Room room :eventManager.getAllRooms()) {
            allRoomNames.add(room.getName());
        }
        return allRoomNames;
    }

    /**
     * @return List<String> list of all dates for all events.
     */
    public List<String> getAllDates() {
        List<String> dates = new ArrayList<>();

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        for (LocalDate date : eventManager.getConferenceDates()) {
            dates.add(date.format(formatter));
        }
        return dates;
    }

    /**
     * Precondition: this method is only accessible to the organizer
     * The options for event manager handler are : "Create Event", "Reschedule Event", "Create Room",
     * "Create Speaker Account"
     * These have associated index from 0 to 3 and will be sent to different handlers
     * like handleCreate Event
     * handleRescheduleEvent
     * handleCreateRoom
     * handleCreateSpeaker
     */
    public void handleEventManager() {
        try {
            List<String> eventManagerOptions = Arrays.asList("Create Event", "Edit Events", "Create Room");

            presenter.printOptions(eventManagerOptions);
            presenter.printIndexRequest();
            String input = parser();
            int index = checkInt(input, 3);
            if (index == 0) { handleCreateEvent(); }
            else if (index == 1) { handleRescheduleEventOptions(); }
            else if (index == 2) { handleCreateRoom(); }
        } catch(Exception e) {
            handleEventManager();
        }
    }

    /**
     * Prompts user as to whether this is a VIP Event
     * Prints options of "yes" or "no"
     * Parses input and if any exceptions occur, restarts process
     * @return decision input was "yes"
     */
    public boolean handleEventVip() {
        try {
            presenter.print("Will this be a VIP Event?");
            presenter.printYesNoRequest();
            String decision = checkCommand(parser(), Arrays.asList("yes", "no"));
            return decision.equals("yes");

        }catch(Exception e) {
            return handleEventVip();
        }
    }

    /**
     * Prompts user to select Speaker for Event in creation.
     * Prints a list of current Speakers at conference and prompts user for input
     * Also prompts user to press enter for a speaker-less event
     * Also prompts user to enter multiple Speakers by separating their IDs with a comma.
     * Parses input and adds Speaker(s) to list.
     * If input is invalid of exceptions occur, restarts process.
     * Checks Speaker(s) list for Covid-19 status, if Speaker(s) is / are positive, warns user.
     * @return selected Speaker
     */
    public List<String> handleEventSpeakers() {
        try {
            List<String> userIds = new ArrayList<>();
            for (User user : userManager.getAllUserType(2)) {
                userIds.add(user.getUserId());}
            presenter.print("Here are all the speakers at the conference:");
            presenter.printList(userIds);
            presenter.printRequest(
                    "Please enter the Speaker's ID.\n" +
                            "If you don't have a speaker for this event press enter. \n" +
                            "If you have multiple speakers for this event separate their names with a comma (no space).");
            String speakerIdInput = parser();

            List<String> speakerIds = new ArrayList<>(Arrays.asList(speakerIdInput.split(",", 100)));

            for (String speakerId: speakerIds) {
                handleExceptions(speakerId);
                if(speakerId.equals("") && speakerIds.size() == 1){
                    speakerIds.remove(0);
                    break;
                }
                else if (!userManager.isUserType(speakerId, 2)) {
                    presenter.printError(speakerId + " is not a valid Speaker ID.");
                    speakerIds = handleEventSpeakers();
                }
            }
            List<String> covidCarriers = new ArrayList<>();
            for (String speakerId : speakerIds) {
                if (userManager.getUser(speakerId).getCovidStatus()) {
                    covidCarriers.add(speakerId);
                }
            }
            if (!covidCarriers.isEmpty()) {
                presenter.printCovidSpeakerWarning(covidCarriers);
                handleEventSpeakers();
            }

            return speakerIds;
        } catch(Exception e) {
            return handleEventSpeakers();
        }
    }

    /**
     * Prompts Organizer to select a Room for the event in creation.
     * If no Room exists at the conference, calls handleCreateRoom and redirects user to create room.
     * Prints existing rooms and prompts user for input of index of room.
     * Parses selection and checks if input is valid, if not restarts the process
     * @return selected room
     */
    public UUID handleEventRoom() {
        try {
            if (getAllRoomNames().isEmpty()) {
                presenter.print("There are no rooms at this conference. Redirecting to Create Room.");
                handleCreateRoom();
            }

            presenter.printOptions(getAllRoomNames());
            presenter.printRoomOption();
            presenter.printIndexRequest();
            String inputRoom = parser();
            int roomIndex = checkInt(inputRoom, getAllRoomNames().size());

            return eventManager.getRoomByIndex(roomIndex).getId();

        } catch(Exception e) {
            return handleEventRoom();
        }
    }

    /**
     * Prompts Organizer to select a EventDate for the event in creation.
     * If there are no dates at the conference, prompts user to input a new EventDate in the required format
     * Otherwise, displays existing dates and prompts user to select an existing EventDate or create a new one
     * If user selects to select an existing date prompts user to input index of EventDate
     * Parses selection and checks if input EventDate is valid, if not restarts the process
     * @return selected Date
     */
    public LocalDate handleEventDate() {
        try {
            LocalDate date;
            String inputDate;
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            if (getAllDates().isEmpty()) {
                presenter.print("There are no dates scheduled for the conference. ");
                presenter.printRequest(
                        "Please input a new date to hold the event on " +
                                "(in yyyy-MM-dd format).");
                inputDate = parser();
                date = LocalDate.parse( inputDate, formatter);

            }
            else {
                for(String dates: getAllDates()){
                    presenter.print(dates);
                }
                presenter.print("Press 1 to pick from dates displayed above, if you want a new date press 0");
                String inputType = parser();
                int inputType2 = checkInt(inputType,2);

                if(inputType2 == 1){
                    presenter.printOptions(getAllDates()); //gives options for dates
                    presenter.printDateOption();
                    presenter.printIndexRequest();
                    inputDate = parser();
                    handleExceptions(inputDate);
                    int dateIndex = checkInt(inputDate, getAllDates().size());
                    String dateString = getAllDates().get(dateIndex);
                    date = LocalDate.parse( dateString, formatter);
                }
                else{
                    presenter.printRequest(
                            "Please input a new date to hold the event on " +
                                    "(in yyyy-MM-dd format).");
                    inputDate = parser();
                    date = LocalDate.parse( inputDate, formatter);
                }
            }
            return date;

        } catch(Exception e) {
            return handleEventDate();
        }
    }

    /**
     * Prompts Organizer to select a EventTime for the event in creation.
     * Displays existing timeslots and prompts user create a new one in the required format
     * Parses selection and checks if input Time is valid, if not restarts the process
     * @return selected Time
     */
    public List<LocalDateTime> handleEventTime(LocalDate date, UUID roomId) {
        try {

            List<Event> allEvents = eventManager.getAllEvents();
            presenter.print("Current time slots taken in this room: ");
            for (Event event:allEvents){
                if(event.getStartTime().getYear() == date.getYear() && event.getStartTime().getMonth() ==
                        date.getMonth() &&event.getStartTime().getDayOfMonth() == date.getDayOfMonth() &&
                        event.getRoomId() == roomId){
                    presenter.print(event.getStartTime().toString().split("T", 2)[1] + "-" +
                            event.getEndTime().toString().split("T", 2)[1]);
                }
            }
            presenter.printRequest(
                    "Please enter the start and end time for this event (in HH:mm-HH:mm format, where the start time" +
                            "is < the end time).");
            String inputTime = parser();

            String[] timeInputs = inputTime.split("-", 2);
            int startTimeHour = Integer.parseInt(timeInputs[0].split(":", 2)[0]);
            int startTimeMin = Integer.parseInt(timeInputs[0].split(":", 2)[1]);
            int endTimeHour = Integer.parseInt(timeInputs[1].split(":", 2)[0]);
            int endTimeMin = Integer.parseInt(timeInputs[1].split(":", 2)[1]);

            LocalDateTime startTime = LocalDateTime.of(date.getYear(), date.getMonthValue(),date.getDayOfMonth(), startTimeHour, startTimeMin);
            LocalDateTime endTime = LocalDateTime.of(date.getYear(), date.getMonthValue(),date.getDayOfMonth(), endTimeHour, endTimeMin);

            if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
                return handleEventTime(date, roomId);
            }

            return Arrays.asList(startTime, endTime);

        } catch(Exception e) {
            return handleEventTime(date, roomId);
        }
    }

    /**
     * Prompts user for capacity of Event in creation
     * Warns the user that capacity should be under the capacity of the Room it is in
     * Checks whether input is valid and checks if it is an integer
     * @param roomId selected Room's ID
     * @return capacity
     */
    public int handleEventSize(UUID roomId) {
        try{
            presenter.print("Please input the capacity of the event (Note: It should be <= the capacity of the room: "
                    + eventManager.getRoom(roomId).getCapacity()+")");
            presenter.printInput();
            String input = parser();
            handleExceptions(input);
            return checkInt(input, 100000);

        }catch(Exception e) {
            return handleEventSize(roomId);
        }
    }


    /**
     * Lets organizer creates an event
     * If the event exists prints error msg "Event name already exists."
     * input speaker id and check validity if not restart
     * request the ids and checks the validity of the IDs
     * gets all the rooms in the conference
     * no rooms there so it creates rooms
     * after that gets all rooms and the request is completed for rooms
     * and now the organizer creates the event
     */
    public void handleCreateEvent() {
        try {
            presenter.printRequest("Please enter the event's name.");
            String eventName = parser();
            handleExceptions(eventName);
            if (eventManager.getAllEventNames().contains(eventName)) {
                presenter.printError("Event name already exists.");
                handleCreateEvent();
            }

            UUID roomId = handleEventRoom();
            boolean isVipEvent = handleEventVip();
            List<String> speakerIds = handleEventSpeakers();
            LocalDate date = handleEventDate();
            List<LocalDateTime> timeSlot = handleEventTime(date, roomId);
            LocalDateTime startTime = timeSlot.get(0);
            LocalDateTime endTime = timeSlot.get(1);
            int size = handleEventSize(roomId);

            if (!createEvent(eventName, startTime, endTime, speakerIds, roomId, size, isVipEvent)) {
                presenter.print("This event can't be created. " +
                        "\nThis could be because the event time is not formatted " +
                        "correctly, conflicts with another event, or it conflicts with the speaker(s)' schedule, " +
                        "or the speaker specified doesn't exist. \nPlease try again or type exit.");
                handleCreateEvent();
            }
            else {
                presenter.printSuccess("Event has been created.");
            }

            mainMenu.handleMainMenu();

        } catch (Exception e) {
            handleCreateEvent();
        }
    }

    /**
     * gets all the events at the conference and
     * the user is asked to reschedule or remove an event
     * if there are no events in the conference, user is
     * asked to create a new event
     */
    public void handleRescheduleEventOptions() {
        try {
            if (eventManager.getAllEventNames().isEmpty()) {
                presenter.print("There are no events to reschedule or remove. Redirecting to Create Event.");
                handleCreateEvent();
            }
            presenter.print("Current events:");
            presenter.printScheduleNoCounter(eventManager.getAllEvents());

            presenter.printRequest("Please enter 'reschedule' to reschedule an event or 'remove' to cancel an event.");
            String input = parser();
            String decision = checkCommand(input, Arrays.asList("reschedule", "remove"));

            presenter.printRequest(
                    "Please enter 'search' to look for an event. Press enter to choose from the entire list.");

            String decision1 = checkCommand(parser(), Arrays.asList("search", ""));
            Event event;
            if(decision1.equals("search")) {
                List<String> eventStrings = handleSearch(eventManager.getAllEventStrings());
                presenter.printIndexRequest();
                int index = checkInt(parser(), eventStrings.size());

                event = eventManager.getEventByString(eventStrings.get(index));
            }
            else {
                presenter.printSchedule(eventManager.getAllEvents());
                presenter.printIndexRequest();
                String inputEvent = parser();
                int eventIndex = checkInt(inputEvent, eventManager.getAllEventNames().size());
                event = eventManager.getEventByIndex(eventIndex);
            }

            if (decision.equals("reschedule")) { handleEventReschedule(event); }
            else { handleEventRemove(event); }

        } catch(Exception e) {
            handleRescheduleEventOptions();
        }
    }

    /**
     * The event is rescheduled here
     * eventScheduler is called to get all events
     * user inputs allows them to choose the event to reschedule
     * program asks for the new date and room of the event
     * the user can choose from a list of potential timeslots
     * rescheduling is checked by eventScheduler
     * print the updating of the event and notify success
     */
    public void handleEventReschedule(Event event) {
        try {
            boolean isVipEvent = event.getIsVipEvent();
            UUID roomId = handleEventRoom();
            LocalDate date = handleEventDate();
            List<LocalDateTime> timeSlot = handleEventTime(date, roomId);
            LocalDateTime startTime = timeSlot.get(0);
            LocalDateTime endTime = timeSlot.get(1);
            int size = handleEventSize(roomId);

            if (!rescheduleEvent(event.getEventId(),
                    roomId,startTime,endTime,size, isVipEvent)) {
                presenter.print("This event can't be rescheduled." +
                        "\nThis could be because the new time conflicts with another event, or it conflicts with the speaker(s)' schedule." +
                        "\nPlease try again or type exit.");
                handleRescheduleEventOptions();
            }
            else {
                presenter.printSuccess("Event has been rescheduled.");
            }

            mainMenu.handleMainMenu();

        } catch (Exception e) {
            handleEventReschedule(event);
        }
    }

    /**
     * removes the events which are to be handled
     * gets the index
     * removes the event from the eventScheduler object
     * The event is removed
     */
    public void handleEventRemove(Event event) {
        try {
            removeEvent(event.getEventId());

            presenter.printSuccess("Event has been removed.");
            mainMenu.handleMainMenu();

        } catch(Exception e) {
            handleEventRemove(event);
        }

    }

    /**
     *Creates a room with the help of eventScheduler
     * If error occur due to wrong input put the method is called again
     */
    public void handleCreateRoom() {
        try {
            presenter.printRequest("Please enter the name of the new room.");
            String roomName = parser();

            presenter.print("Please input the capacity of the room");
            presenter.printInput();
            String input = parser();
            int capacity = checkInt(input, 100000);


            if(eventManager.createRoom(roomName, capacity)) {
                presenter.printSuccess("New room has been added.");
                handleEventManager();
            }
            else {
                presenter.printError("Room could not be added");
                handleCreateRoom();
            }

        } catch (Exception e) {
            handleCreateRoom();
        }

    }


}

