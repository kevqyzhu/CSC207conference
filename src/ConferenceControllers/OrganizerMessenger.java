package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.User;
import UseCases.ConversationManager;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class OrganizerMessenger extends Announcer{
    private int type;


    /**
     * @param convManager a ConversationManager object that is passed in so this object can use it
     * @param userManager a UserManager object that is passed in so this object can use it
     * @param mainMenu the main menu of the program
     */

    public OrganizerMessenger(ConversationManager convManager, UserManager userManager, MainMenu mainMenu) {
        super(convManager, userManager, mainMenu);

    }

    /**
     * For Organizer, displays list of all user types they can message
     * Prompts user for input of index
     * Parses index input and checks if it is valid then assigns type to each user and calls handleUsers
     * If exceptions occur, restarts process
     */
    public void handleMessenger() {
        try {
            List<String> organizerOptions = Arrays.asList("Speakers", "Attendees", "Admin", "VIP Users");
            presenter.printOptions(organizerOptions);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, 4);
            if (index == 0) {
                type = 2;
            }
            else if (index == 1) {
                type = 3;
            }
            else if (index == 2) {
                type = 4;
            }
            else if (index == 3) {
                type = 5;
            }
            handleUsers();
        } catch(Exception e) {
            handleMessenger();
        }
    }

    /**
     * gets a list of users by specific type
     * @return returns a list of Ids that represent the Users
     */
    public List<String> getAllUserType() {
        /*
        Returns a list containing all speaker ids
         */
        return userManager.getAllUserTypeIds(type);
    }

    /**
     * selects a User to send a message to and opens Conversation with that User based on userIndex
     * @param userIndex index of a user
     */
    public void chooseOneUserType(int userIndex) {
        String userId = getAllUserType().get(userIndex);
        User receiver = userManager.getUser(userId);
        convManager.openCurrentConversation(userManager.getCurrentUser(), receiver);
    }

    /**
     * For Attendee
     *get the presenter  organizerMessenger from this object get all attendees
     * input string to send message
     * decision to choose the user or all
     * if the option is all then send to all
     * else sent to chosen user
     * if any exceptions occur at any point then restart the process
     */
    /*
     * Entities.Organizer ConferenceControllers.ConferenceControllers.Messenger Handlers
     */
    public void handleUsers() {
        try {
            if (getAllUserType().isEmpty()) {
                presenter.print("There are no users of this type.");
                handleMessenger();
            }
            presenter.printList(getAllUserType());
            presenter.printRequest("Please enter 'choose' to message a recipient from the users listed above or " +
                    "'all' to send a message to all users." +
                    "\nEnter 'search' to search for a single user.");

            String input = parser();
            String decision = checkCommand(input, Arrays.asList("choose", "all", "search"));
            if (decision.equals("choose")) {
                presenter.printOptions(getAllUserType());
                presenter.printIndexRequest();

                input = parser();
                int index = checkInt(input, getAllUserType().size());
                chooseOneUserType(index);
                handleChooseUser();
            }
            else if (decision.equals("search")) {
                String userId = handleSearchUser(getAllUserType());
                chooseOneUser(userId);
                handleChooseUser();
            }
            else {
                handleSendToAll();
            }
        } catch(Exception e) {
            handleUsers();
        }
    }

    /**
     * if all attendee
     *  input for the message
     *  send all the attendees the messages by the organizer of that event
     */
    public void handleSendToAll() {
        try {
            presenter.printSendMessage();

            String input = parser();
            sendToAll(input, getAllUserType());
            convManager.closeCurrentConversation();
            mainMenu.handleMainMenu();
        } catch(Exception e) {
            handleSendToAll();
        }
    }

}
