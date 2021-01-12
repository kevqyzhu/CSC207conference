package ConferenceControllers.MainMenu;

import ConferenceControllers.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.ArrayList;


public class VIPUserMainMenu extends MainMenu {
    private final UserMessenger messenger;
    private final EventSignup eventSignup;
    private final RequestHandler requestHandler;

    public VIPUserMainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
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
            ArrayList<String> attendeeOptions = new ArrayList<>();
            attendeeOptions.add("Messages");
            attendeeOptions.add("My Events");
            attendeeOptions.add("My VIP Events");
            attendeeOptions.add("Event Signup");
            attendeeOptions.add("My Requests");
            attendeeOptions.add("View Archived Messages");
            presenter.printMenuRequest();
            presenter.printOptions(attendeeOptions);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, 6);
            if (index == 0) { messenger.handleMessenger(); }
            else if (index == 1) {
                eventSignup.handleEvents();
            }
            else if(index == 2){
                eventSignup.handleVipEvents();
            }
            else if(index == 3){
                eventSignup.handleEventSignup();
            }
            else if (index == 4){
                requestHandler.handleAttendeeRequestOptions();
            }
            else if (index == 5) {messenger.handleMessengerViewArchivedConversations();}
        } catch(Exception e) {
            handleMainMenu();
        }
    }
}