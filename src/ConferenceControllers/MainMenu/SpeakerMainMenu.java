package ConferenceControllers.MainMenu;

import ConferenceControllers.Decision;
import ConferenceControllers.SpeakerMessenger;
import ConferenceControllers.UserDecision;
import ConferenceControllers.UserMessenger;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class SpeakerMainMenu extends MainMenu {
    private final UserMessenger messenger;
    private final SpeakerMessenger speakerMessenger;


    public SpeakerMainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
        super(userManager, eventManager, conversationManager);
        Decision decision = new UserDecision();
        messenger = new UserMessenger(conversationManager, userManager, this, decision);
        speakerMessenger = new SpeakerMessenger(conversationManager,userManager, eventManager, this);

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
                List<String> speakerOptions = Arrays.asList("Messages", "Talks", "View Archived Messages");
                presenter.printMenuRequest();
                presenter.printOptions(speakerOptions);
                presenter.printIndexRequest();

                String input = parser();
                int index = checkInt(input, 3);
                if (index == 0) { messenger.handleMessenger(); }
                else if (index == 1) { speakerMessenger.handleSpeakerMessenger(); }
                else if (index == 2) {messenger.handleMessengerViewArchivedConversations();}
        } catch(Exception e) {
            handleMainMenu();
        }
    }
}
