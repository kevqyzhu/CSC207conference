package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.Conversation;
import Entities.Event;
import Entities.Speaker;
import Entities.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeakerMessenger extends Announcer {
    private final EventManager eventManager;
    private Event currentEvent;

    /**
     * @param convManager a ConversationManager object that is passed in so this object can use it
     * @param userManager a UserManager object that is passed in so this object can use it
     * @param eventManager a EventManager object that is passed in so this object can use it
     * @param mainMenu the program's main menu
     */
    public SpeakerMessenger(ConversationManager convManager, UserManager userManager, EventManager eventManager, MainMenu mainMenu) {
        super(convManager, userManager, mainMenu);
        this.eventManager = eventManager;
    }

    /**
     * gets a list of events that the speaker is speaking at
     * @return returns a list of Event objects to represent the events the a speaker is speaking at
     */
    public List<Event> getEvents() {
        List<Event> event1s = eventManager.getEventsBySpeaker((Speaker) userManager.getCurrentUser());
        return new ArrayList<>(event1s);
    }

    /**
     *  gets a list of attendee Ids in the current event
     * @return returns a list of Strings that represents the UserIds of attendees attending the current event
     */
    public List<String> getUserIdsInEvent() {
        return currentEvent.getAttendeeIds();
    }

    /**
     *  gets the number of people currently attending the current event
     * @return returns an Integer that represents the current number of attendees at the current event
     */
    public int getEventSize() {
        return getUserIdsInEvent().size();
    }

    /**
     * starts a conversation with one user based on the userIndex
     * @param userIndex an Integer that represents a specific user from the user index
     * @return returns a conversation object
     */
    public Conversation chooseOneUser(int userIndex) {
        List<String> userIds = getUserIdsInEvent();
        String userId = userIds.get(userIndex);
        User receiver = userManager.getUser(userId);
        return convManager.openCurrentConversation(userManager.getCurrentUser(), receiver);
    }

    /**
     * For Speaker
     * get the presenter speakerMessenger from this object to get all events
     * then displays the all talks for speaker based on event
     * asks user if they want to choose specific attendee or all attendees to send a message
     * parses String input and calls appropriate methods based on choice made
     */
    public void handleSpeakerMessenger() {
        try {
            List<Event> events = getEvents();
            if (events.size() == 0) {
                presenter.print("You are not speaking at any talks. Routing back to the main menu.");
                mainMenu.handleMainMenu();
            }
            presenter.print("Select the talk you'd like to look at.");
            presenter.printTalks(events);
            String input = parser();
            currentEvent = this.eventManager.getEventByIndex(Integer.parseInt(input));
            if (currentEvent.getAttendeeIds().size() == 0) {
                presenter.print("There are no attendees for this event yet to message. Routing back.");
                handleSpeakerMessenger();
            }
            presenter.printList(getUserIdsInEvent());
            presenter.printRequest("Please enter 'choose' to message a single recipient from the attendees listed above or " +
                    "'all' to send a message to all attendees at your talk." +
                    "\nEnter 'search' to search for a single user.");

            input = parser();
            String decision = checkCommand(input, Arrays.asList("choose", "all", "search"));
            if (decision.equals("choose")) {
                presenter.printOptions(getUserIdsInEvent());
                presenter.printIndexRequest();

                input = parser();
                chooseOneUser(checkInt(input, getEventSize()));
                handleChooseUser();

            } else if (decision.equals("search")) {
                String userId = handleSearchUser(getUserIdsInEvent());
                chooseOneUser(userId);
                handleChooseUser();
            } else {
                handleSendToAll();
            }
        } catch(Exception e) {
            handleSpeakerMessenger();
        }
    }

    /**
     * if all Attendees are chosen to send message to
     * requests String input of message
     * updates Conversation using sentToAll method with contents of message
     * returns to main menu
     */
    private void handleSendToAll() {
        try {
            presenter.printSendMessage();
            String input = parser();
            sendToAll(input, getUserIdsInEvent());
            convManager.closeCurrentConversation();
            mainMenu.handleMainMenu();
        } catch(Exception e) {
            handleSendToAll();
        }
    }
}
