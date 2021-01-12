package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.Event;
import Entities.Attendee;
import Entities.Room;
import UseCases.EventManager;
import UseCases.UserManager;

import java.awt.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.io.File;

public class EventSignup extends ConferenceController{
    // Precondition user is a valid user
    UserManager userManager;
    EventManager eventManager;
    /**
     * Sign up the user to the event
     */
    public EventSignup(UserManager userManager, EventManager eventManager, MainMenu mainMenu) {
        super(mainMenu);
        this.userManager = userManager;
        this.eventManager = eventManager;
    }

    /**
     * Returns a list of events the current user (from UseCases.UserManager) is scheduled to attend/has attended.
     * @return an ArrayList<Event> containing Events the current user is set to attend.
     */
    public ArrayList<Event> getCurrentUserSchedule() {
        ArrayList<Event> listOfEvents = new ArrayList<>();
        for (UUID eventId: userManager.getCurrentUser().getEventIds()) {
            listOfEvents.add(eventManager.getEvent(eventId));
        }

        return listOfEvents;
    }

    /**
     * Generates an HTML file with a list of events the current user is scheduled to attend/has attended.
     */
    public void exportSchedule() throws Exception {
        ArrayList<Event> listOfEvents = getCurrentUserSchedule();
        StringBuilder eventSchedule = new StringBuilder();
        for (Event e : listOfEvents) {
            eventSchedule.append("<p>").append(e.toString()).append("</p>");
        }
        String html = "<html>" +
                "<head><title>Schedule</title></head>" +
                "<body>" +
                "<h2> This is your schedule: </h2>" +
                eventSchedule +
                "</body>" +
                "</html>";

        File file = new File("schedule.html");
        FileWriter writer = new FileWriter(file);
        writer.write(html);
        writer.close();
        Desktop.getDesktop().browse(file.toURI());
    }

    /**
     * This will get the event and attendee object for these Ids, then it will update the user schedule based on the
     *         events times, then will call the addUser method from UseCases.EventManager which will update Ids on both ends.
     * @param eventId a UUID that is unique to a specific event
     * @param attendeeId a String of the unique username for the attendee
     */
    public boolean addUser(UUID eventId, String attendeeId) {
        Event event = eventManager.getEvent(eventId);
        Attendee attendee = userManager.getAttendee(attendeeId);

        if (event == null || attendee == null) {
            return false; //event or attendee doesn't exist might need to split into two different cases for presenters
        }
        if(event.getAttendeeIds().size() >= eventManager.getRoom(event.getRoomId()).getCapacity() ||
                event.getAttendeeIds().size() >= event.getCapacity()) {
            return false;
        }

        if (attendee.isAvailable(event.getStartTime(),event.getEndTime()) && !event.getAttendeeIds().contains(attendeeId)) {
            attendee.addTime(event.getStartTime(),event.getEndTime());
            eventManager.addUser(attendee,event);
            return true;
        }
        return false; //if time slot is already full for the user return false
    }

    /**
     * Adds current user to the specified event.
     * @param eventId a UUID that is unique to a specific event
     */
    public boolean addUser(UUID eventId) {
        return addUser(eventId, userManager.getCurrentUser().getUserId());
    }

    /**
     * This will get the event and attendee object for these Ids, then it will update the user schedule based on the
     *         events times, then will call the removeUser method from UseCases.EventManager which will remove Ids on both ends.
     *         This method must verify the user is attending the event before making changes.
     * @param eventId a UUID that is unique to a specific event
     * @param attendeeId a UUID that uniquely identifies an attendee
     * @return boolean representing if the user was removed or not
     */
    public boolean removeUser(UUID eventId, String attendeeId) {

        Event e = eventManager.getEvent(eventId);
        Attendee a = userManager.getAttendee(attendeeId);
        if(e == null || a == null) {
            return false; // case where e or a doesn't exist, maybe split for presenters in the future
        }
        if (a.removeTime(e.getStartTime(),e.getEndTime())) {
            eventManager.removeUser(a,e);
            return true;
        }
        return false;//case where user didn't have anything during this time
        //I guess we could run into an issue if a user was signed up for another event during the same time, maybe we
        //should also check the room id for phase 2 but since phase 1 only has one room this should be fine for now
    }

    /**
     * removes the current user from specified event.
     * @param eventId a UUID that uniquely identifies an event.
     * @return boolean representing if the user was removed or not
     */
    public boolean removeUser(UUID eventId) {
        return removeUser(eventId, userManager.getCurrentUser().getUserId());
    }

    /**
     * indicates if an attendee can be added to the specified event, or if its full.
     * @param eventId a UUID that uniquely identifies an event.
     * @return boolean indicates if an attendee can be added to the specified event, or if its full.
     */
    public boolean canAddAttendee(UUID eventId) {
        return eventManager.canAddAttendee(eventId);
    }

    /**
     * Returns a list of events that an attendee has room in their schedule to attend.
     * @return ArrayList<Event> a list of events that the current user can attend
     */
    public ArrayList<Event> getEventsCurrentAttendeeCanAttend() {
        Attendee attendee = this.userManager.getAttendee(this.userManager.getCurrentUser().getUserId());
        ArrayList<Event> events = new ArrayList<>();
        for (Room room: this.eventManager.getSchedule().keySet()) {
            for (Event event: this.eventManager.getSchedule().get(room)) {
                if (attendee.isAvailable(event.getStartTime(), event.getEndTime()) &&
                        ((attendee.isVIPUser() || !event.getIsVipEvent()))) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    /**
     * Displays the schedule for the user
     * If the schedule is empty then your schedule is displayed
     * If the schedule is not empty then the input is parsed from the user
     * exceptions are handled by handleException()
     * Decision is given
     * To sign up or cancel the event
     * if the input it sign up the event is sign up for
     * else it we want to cancel the event then cancel option is typed
     */
    public void handleEvents() {
        try {
            ArrayList<Event> schedule = getCurrentUserSchedule();
            if (schedule.isEmpty()) {
                presenter.printNoSchedule();
                handleEventSignup();
            }
            presenter.print("All Events:");
            handleSchedule(schedule);

        } catch (Exception e) {
            handleEvents();
        }
    }

    /**
     * Displays the schedule of VIP events for the user
     * If the schedule is empty then your schedule is displayed
     * If the schedule is not empty then the input is parsed from the user
     * exceptions are handled by handleException()
     * Decision is given
     * To sign up or cancel the VIP event
     * if the input it sign up the event is sign up for
     * else it we want to cancel the event then cancel option is typed
     */
    public void handleVipEvents() {
        try {
            if (getCurrentUserSchedule().isEmpty()) {
                presenter.printNoSchedule();
                handleEventSignup();
            }
            List<Event> schedule = eventManager.getVipEvents(getCurrentUserSchedule());
            if (schedule.isEmpty()) {
                presenter.print("You have no VIP events.");
                handleEvents();
            }
            presenter.print("VIP Events:");
            handleSchedule(schedule);
        } catch (Exception e) {
            handleVipEvents();
        }
    }

    /**
     * Allows user to view, sign up or cancel or export their events
     * Prompts user to export their schedule to HTML or view it within the program
     * Also asks user to enter input of "signup" or "cancel" to either signup for a new event or cancel an event
     * If "signup" is parsed, calls handleEventSignup or if "cancel" is parsed, calls handleEventCancel
     */
    public void handleSchedule (List<Event> schedule ) {
        try {
            presenter.printRequest(
                    "Please enter 'export' to export your schedule to HTML or 'show' to look at your schedule.");
            String input = parser();
            String decision = checkCommand(input, Arrays.asList("export", "show"));
            if (decision.equals("export")) {
                exportSchedule();
                mainMenu.handleMainMenu();
            }

            ArrayList<Event> schedule1 = new ArrayList<>(schedule);
            presenter.printSchedule(schedule1);
            presenter.printRequest("Please enter 'signup' to signup for a new event, " +
                    "or enter 'cancel' to remove an event from your schedule.");

            input = parser();
            decision = checkCommand(input, Arrays.asList("signup", "cancel"));
            if (decision.equals("signup")) { handleEventSignup(); }
            else { handleEventCancel(); }
        }catch (Exception e) {
            handleSchedule(schedule);
        }
    }

    /**
     * The sign up option is created if no events go to main menu
     * else the input is parsed from the user this input the event they want to signup for
     * this would handle the event signup and add to the schedule of the user
     */
    public void handleEventSignup() {
        try {
            if (userManager.getCurrentUser().getCovidStatus()) {
                presenter.print("You have been marked by an admin user as a COVID carrier. " +
                        "You have been removed from all events and you are no longer allowed to sign up for any more." +
                        "\nPlease check your messages for any updates.");
                mainMenu.handleMainMenu();
            }

            ArrayList<Event> schedule = getEventsCurrentAttendeeCanAttend();
            if (schedule.isEmpty()) {
                presenter.printNoEvents();
                mainMenu.handleMainMenu();
            }
            ArrayList<Event> schedule1 = new ArrayList<>(schedule);
            Event event;

            presenter.printRequest("Please enter 'search' to look for an event. Press enter to choose from the entire list.");
            String decision = checkCommand(parser(), Arrays.asList("search", ""));
            if (decision.equals("search")) {
                List<String> eventNames = handleSearch(eventManager.getEventNames(getEventsCurrentAttendeeCanAttend()));
                presenter.printIndexRequest();
                int index = checkInt(parser(), eventNames.size());

                event = eventManager.getEventByName(eventNames.get(index));
            } else {
                presenter.printSchedule(schedule1);
                presenter.printIndexRequest();

                String input = parser();
                int index = checkInt(input, schedule.size());
                event = schedule.get(index);
            }

            if (addUser(event.getEventId())) {
                presenter.printSuccess("You have successfully enrolled in " + event.getName());
                mainMenu.handleMainMenu();
            } else {
                presenter.printError("Unable to enroll in " + event.getName());
                handleEventCancel();
            }
        } catch (Exception e) {
            handleEventSignup();
        }
    }

    /**
     *  The sign up option is created if no events go to main menu
     * else the input is parsed from the user this input the event they want to cancellation for
     * this would handle the event cancel from the schedule of the user
     * If error occur catch and re try
     */
    public void handleEventCancel() {
        try {
            ArrayList<Event> schedule = getCurrentUserSchedule();
            ArrayList<Event> schedule1 = new ArrayList<>(schedule);
            presenter.printSchedule(schedule1);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, schedule.size());
            if (removeUser(schedule.get(index).getEventId())) {
                presenter.printSuccess("Enrollment in " +
                        schedule.get(index).getName() + " has been cancelled.");
                mainMenu.handleMainMenu();
            } else {
                presenter.printError(schedule.get(index).getName() + " could not be cancelled.");
                handleEventCancel();
            }
        } catch (Exception e) {
            handleEventCancel();
        }
    }
}
