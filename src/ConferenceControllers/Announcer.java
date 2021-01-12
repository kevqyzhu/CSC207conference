package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.Conversation;
import Entities.User;
import UseCases.ConversationManager;
import UseCases.UserManager;

import java.util.List;

public abstract class Announcer extends ConferenceController{
    public final ConversationManager convManager;
    public final UserManager userManager;

    public Announcer(ConversationManager convManager, UserManager userManager, MainMenu mainMenu) {
        super(mainMenu);
        this.convManager = convManager;
        this.userManager = userManager;
    }

    /**
     * starts a conversation with one user based on the userId
     * @param userId an Id of a specific user
     * @return returns a conversation object
     */
    public Conversation chooseOneUser(String userId) {
        User receiver = userManager.getUser(userId);
        return convManager.openCurrentConversation(userManager.getCurrentUser(), receiver);
    }

    /**
     * sends a message to one user
     * @param message a String that represents a message
     */
    public void sendToOne(String message) {
        convManager.updateCurrentConversation(userManager.getCurrentUser(), message);
    }

    /**
     * sends message to all Users of a specific type
     * @param message a String that represents a message
     * @param userIds a List of user IDs to send a message to
     */
    public void sendToAll(String message, List<String> userIds) {
        if (!userIds.isEmpty()) {
            for (String userId : userIds) {
                User receiver = userManager.getUser(userId);
                convManager.openCurrentConversation(userManager.getCurrentUser(), receiver);
                convManager.updateCurrentConversation(userManager.getCurrentUser(), message);
            }
        }
    }

    /**
     * if single Attendee is chosen to send message to
     * displays list of Attendees and requests String input of index to choose
     * parses index and selects Attendee then requests String input of message
     * updates Conversation with contents of message and returns to main menu
     */
    public void handleChooseUser() {
        try {
            presenter.printSendMessage();
            String input = parser();
            sendToOne(input);
            convManager.closeCurrentConversation();
            mainMenu.handleMainMenu();

        } catch(Exception e) {
            handleChooseUser();
        }
    }
}
