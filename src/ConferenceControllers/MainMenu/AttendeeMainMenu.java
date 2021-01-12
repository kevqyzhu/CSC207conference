package ConferenceControllers.MainMenu;

import ConferenceControllers.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class AttendeeMainMenu extends MainMenu {
    private final UserMessenger messenger;
    private final EventSignup eventSignup;
    private final RequestHandler requestHandler;

    public AttendeeMainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
        super(userManager, eventManager, conversationManager);
        Decision decision = new UserDecision();
        messenger = new UserMessenger(conversationManager, userManager, this, decision);
        eventSignup = new EventSignup(userManager, eventManager, this);
        requestHandler = new RequestHandler(userManager, this);
    }

    /**
     * Displays the different menus for the different users
     * Organizers have 4 options: messages, Event Manager, Attendee and Speakers
     * Speaker have 2 options: messages and talks
     * Attendees have 2 options: Messages and Events
     * for each of these an input by the user is given to navigate to the new tab
     */
    public void handleMainMenu() {
        try {
                List<String> attendeeOptions = Arrays.asList("Messages", "My Events", "Event Signup", "Requests", "View Archived Messages");
                presenter.printMenuRequest();
                presenter.printOptions(attendeeOptions);
                presenter.printIndexRequest();

                String input = parser();
                int index = checkInt(input, 5);
                if (index == 0) { messenger.handleMessenger(); }
                else if (index == 1) { eventSignup.handleEvents(); }
                else if (index == 2) { eventSignup.handleEventSignup(); }
                else if (index == 3) { requestHandler.handleAttendeeRequestOptions();}
                else if (index == 4) { messenger.handleMessengerViewArchivedConversations();}
        } catch(Exception e) {
            handleMainMenu();
        }
    }
}
