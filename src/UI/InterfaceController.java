package UI;

import ConferenceControllers.LoginReceiver;
import Gateways.ManagerGateway;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *This is the Interface for the all the controller
 */
public class InterfaceController {
    private final BufferedReader reader;
    private final ManagerGateway managerGateway;
    private final UserManager userManager;
    private final ConversationManager conversationManager;
    private final EventManager eventManager;

    /**
     * Constructor for InterfaceController class. This instantiates the reader parameter as BufferedReader object
     * and the presenter parameter has the object of InterfacePresenter
     */
    public InterfaceController() throws ClassNotFoundException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        managerGateway = new ManagerGateway();
//        vipEventManager = managerGateway.readVM("phase2/vm.ser");
        userManager = managerGateway.readUM("phase2/um.ser");
        conversationManager = managerGateway.readCM("phase2/cm.ser");
        eventManager = managerGateway.readEM("phase2/em.ser");
    }

    /**
     *This is the method that is used to run the whole project.
     * First we instantiate the userManager, conversationManager and EventManager objects
     *  - Create different kinds of users using the userManager object
     *  - state the start and end time for different events
     *  - set the room object and create these rooms
     *  - Create an event using the startTime, endTime , roomID, speakerID and name of the event
     *  - Add Users to events
     */
    public void run() {
        LoginReceiver loginReceiver = new LoginReceiver(userManager, eventManager, conversationManager);
        loginReceiver.handleLogIn();
    }

    /**
     * This is where the program starts
     * InterfaceController is called
     * Object pf Interface controller is created
     * The run method is started
     * @param args main of the program
     */
    public static void main(String[] args) throws ClassNotFoundException {
        InterfaceController ic = new InterfaceController();
        ic.run();
    }
}