package ConferenceControllers;

import ConferenceControllers.MainMenu.*;
import Entities.User;
import Gateways.ManagerGateway;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.io.IOException;

/**
 * This class is what handles and verifies login credentials.
 */
public class LoginReceiver extends ConferenceController{
    UserManager userManager;
    EventManager eventManager;
    ConversationManager conversationManager;
    ManagerGateway managerGateway = new ManagerGateway();

    /**
     * The constructor of LoginReceiver will store an instance of the UserManager class.
     */
    public LoginReceiver(UserManager userManager, EventManager eventManager,
                         ConversationManager conversationManager) {
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.conversationManager = conversationManager;
    }

    /**
     * This method calls a specific main menu based on the login info given in the parameters.
     * @param userid the username inputted at login
     * @param password the password inputted at login
     */
    public void login(String userid, String password) {
        if (userManager.findUserId(userid, password)) {
            User usr = userManager.getUser(userid);
            userManager.setCurrentUser(usr);
            if (usr.isOrganizer()) {
                OrganizerMainMenu organizerMainMenu = new OrganizerMainMenu(userManager, eventManager,
                        conversationManager);
                organizerMainMenu.handleMainMenu();
            } else if (usr.isSpeaker()) {
                SpeakerMainMenu speakerMainMenu = new SpeakerMainMenu(userManager, eventManager, conversationManager);
                speakerMainMenu.handleMainMenu();
            } else if (usr.isVIPUser()) {
                VIPUserMainMenu vipUserMainMenu = new VIPUserMainMenu(userManager, eventManager, conversationManager);
                vipUserMainMenu.handleMainMenu();
            } else if (usr.isAdminUser()) {
                AdminUserMainMenu adminUserMainMenu = new AdminUserMainMenu(userManager, eventManager, conversationManager);
                adminUserMainMenu.handleMainMenu();
            } else { //isAttendee() Switched vip check to be before attendee since VIPUser is a subclass of Attendee
                AttendeeMainMenu attendeeMainMenu = new AttendeeMainMenu(userManager, eventManager, conversationManager);
                attendeeMainMenu.handleMainMenu();
            }
        } else {
            presenter.printError("Username and Password are incorrect. Please try again.");
            handleLogIn();
        }
    }

    /**
     * Logs in the specific user
     * UserType is the type of user which is designated for each user.
     * this would specify the different users logging in to the system and will enables each user to
     * have different menus
     */
    public void handleLogIn() {
        try {
            presenter.printUsernameRequest();
            String username = parser();

            presenter.printPasswordRequest();
            String password = parser();

            login(username, password);

        } catch(Exception e) {
            handleLogIn();
        }
    }

    /**
     * handles exceptions based on keyboard input from user for Login process
     * if "help" is printed calls printLoginHelp
     * if "quit" is printed calls printTerminate and exits the program entirely
     * @param input: Takes in a keyboard input from the terminal
     * @throws Exception
     */
    @Override
    public void handleExceptions(String input) throws Exception {
        if (input.toLowerCase().contains("help")) {
            presenter.printLoginHelp();
            throw new Exception();
        } else if (input.toLowerCase().contains("quit")) {
            presenter.printTerminate();
            exit();
            }
        }

    /**
     * throws IOException when exiting program
     * @throws IOException
     */
    public void exit() throws IOException {
        managerGateway.saveUM(userManager);
        managerGateway.saveCM(conversationManager);
        managerGateway.saveEM(eventManager);
        // managerGateway.saveVM(vipEventManager);
        reader.close();
        System.exit(0);
    }
}


