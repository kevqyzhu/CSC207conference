package ConferenceControllers.MainMenu;

import ConferenceControllers.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class OrganizerMainMenu extends MainMenu {
    private final UserMessenger messenger;
    private final EventScheduler eventScheduler;
    private final OrganizerMessenger organizerMessenger;
    private final UserGenerator generator;
    private final RequestHandler requestHandler;

    public OrganizerMainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
        super(userManager, eventManager, conversationManager);
        Decision decision = new UserDecision();
        generator = new UserGenerator(userManager, this);
        messenger = new UserMessenger(conversationManager, userManager, this, decision);
        eventScheduler = new EventScheduler(eventManager,userManager, this);// needs to change
        requestHandler = new RequestHandler(userManager, this);
        organizerMessenger = new OrganizerMessenger(conversationManager, userManager, this);
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
                List<String> organizerOptions = Arrays.asList("Messages", "Event Manager", "User Announcer",
                        "Create User", "User Requests", "View Archived Messages");
                presenter.printMenuRequest();
                presenter.printOptions(organizerOptions);
                presenter.printIndexRequest();

                String input = parser();
                int index = checkInt(input, 6);
                if (index == 0) { messenger.handleMessenger(); }
                else if (index == 1) { eventScheduler.handleEventManager(); }
                else if (index == 2) { organizerMessenger.handleMessenger(); }
                else if (index == 3) { generator.handleUserGenerator(); }
                else if (index == 4) { requestHandler.handleOrganizerRequestOptions(); }
                else if (index == 5) {messenger.handleMessengerViewArchivedConversations();}
        } catch(Exception e) {
            handleMainMenu();
        }
    }
}
