package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import UseCases.UserManager;

import java.util.Arrays;
import java.util.List;

public class UserGenerator extends ConferenceController {
    private final UserManager userManager;
    int type;

    public UserGenerator(UserManager userManager, MainMenu mainMenu) {
        super(mainMenu);
        this.userManager = userManager;
    }

    /**
     * For Organizer, displays list of all user types they can create
     * Prompts user for input of index
     * Parses index input and checks if it is valid then assigns type to each user and calls handleCreate User
     * If exceptions occur, restarts process
     */
    public void handleUserGenerator() {
        try {
            List<String> organizerOptions = Arrays.asList("Organizers", "Speakers", "Attendees", "Admins", "VIP Users");
            presenter.printOptions(organizerOptions);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, 5);
            if (index == 0) {
                type = 1;
            } else if (index == 1) {
                type = 2;
            } else if (index == 2) {
                type = 3;
            } else if (index == 3) {
                type = 4;
            } else if (index == 4) {
                type = 5;
            }
            handleCreateUser();
        } catch(Exception e) {
            handleUserGenerator();
        }
    }

    /**
     * Creates a user object.
     * @param userId String unique username for the user that is being created
     * @param password String password for this new user
     */
    public void createUser(String userId, String password, int type) {
        if (type == 1) {
            userManager.createOrganizer(userId, password);
        } else if (type == 2) {
            userManager.createSpeaker(userId, password);
        } else if (type == 3) {
            userManager.createAttendee(userId, password);
        } else if (type == 4) {
            userManager.createAdminUser(userId, password);
        } else if (type == 5) {
            userManager.createVIPUser(userId, password);
        }
    }

    /**
     * Creates a user
     * user id is passed and password is passed
     * if the speaker id exits in the system then error will occur and will print "User already exists."
     */
    public void handleCreateUser() {
        try {
            presenter.printRequest("Please enter the new User's username.");
            String id = parser();

            presenter.printRequest("Please enter the new User's password.");
            String password = parser();
            if (userManager.isUserType(id, type)) {
                presenter.printError("User already exists.");
            }
            createUser(id, password, type);
            presenter.printSuccess("User account has been created.");
            mainMenu.handleMainMenu();
        }catch (Exception e) {
            handleCreateUser();
        }
    }
}
