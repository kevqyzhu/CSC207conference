package ConferenceControllers.MainMenu;

import ConferenceControllers.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class AdminUserMainMenu extends MainMenu {
    private final UserMessenger messenger;
    private final UserTracker userTracker;
    private final EventScheduler eventScheduler;

    public AdminUserMainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
        super(userManager, eventManager, conversationManager);
        Decision decision = new AdminUserDecision();
        messenger = new UserMessenger(conversationManager, userManager, this, decision);
        userTracker = new UserTracker(userManager, eventManager, conversationManager, this);
        eventScheduler = new EventScheduler(eventManager, userManager, this);
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
            List<String> adminOptions = Arrays.asList("Messages", "COVID Tracker", "Delete Empty Events",
                    "View Archived Messages");
            presenter.printMenuRequest();
            presenter.printOptions(adminOptions);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, 4);
            if (index == 0) { messenger.handleMessenger(); }
            if (index == 1) { userTracker.handleAdminTracker(); }
            if(index == 2){
                eventScheduler.handleDeleteEmptyEvent();
            }
            if (index == 3) {messenger.handleMessengerViewArchivedConversations();}
        } catch(Exception e) {
            handleMainMenu();
        }
    }
}
