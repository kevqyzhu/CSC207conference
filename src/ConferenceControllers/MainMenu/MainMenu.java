package ConferenceControllers.MainMenu;

import ConferenceControllers.ConferenceController;
import Gateways.ManagerGateway;
import UI.InterfaceController;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.io.IOException;

public abstract class MainMenu extends ConferenceController {
    private final UserManager userManager;
    private final ConversationManager conversationManager;
    private final EventManager eventManager;
    private final ManagerGateway managerGateway = new ManagerGateway();

    public MainMenu(UserManager userManager, EventManager eventManager, ConversationManager conversationManager) {
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.conversationManager = conversationManager;
    }

    public abstract void handleMainMenu();

    /**
     * handles exceptions based on keyboard input from user
     * if "help" is printed called printHelp
     * if "exit" is printed calls printExit and returns to main menu
     * if "quit" is printed calls printTerminate and exits the program entirely
     * @param input: Takes in a keyboard input from the terminal
     * @throws Exception
     */
    @Override
    public void handleExceptions(String input) throws Exception {
        if (input.toLowerCase().contains("help")) {
            presenter.printHelp();
            throw new Exception();
        } else if (input.toLowerCase().contains("exit")) {
            presenter.printExit();
            handleMainMenu();
        } else if (input.toLowerCase().contains("quit")) {
            presenter.printTerminate();
            exit();
        }
    }

    /**
     * The program is terminated
     * Steps taken
     *  - The data is saved in the serializable files cm.ser, um.ser, em.ser
     *  - The managerGateway saves all these data
     *  - The reader is closed
     *  - The program is terminated
     * @throws IOException throws exception if there is an error in reading the text by the buffered reader
     */
    public void exit() throws IOException, ClassNotFoundException {
        managerGateway.saveUM(userManager);
        managerGateway.saveCM(conversationManager);
        managerGateway.saveEM(eventManager);
        InterfaceController interfaceController = new InterfaceController();
        interfaceController.run();
    }
}
