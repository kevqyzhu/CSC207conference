package Gateways;

import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.io.*;

/**
 * Contains a read and save method for each type of use case class.
 */
public class ManagerGateway {

    /**
     * Constructor for ManagerGateway.
     */
    public ManagerGateway() {}

    /**
     * Returns the UserManager object that is stored in the file with the specified fileName. If no instance can be
     * found in such a file then a new UserManager object is returned.
     * @param fileName the file path that the method reads from
     * @return a UserManager object that is stored within fileName
     * @throws ClassNotFoundException if a UserManager object is not found within fileName
     */
    public UserManager readUM(String fileName) throws ClassNotFoundException {

        try {
            InputStream file = new FileInputStream(fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            UserManager um = (UserManager) input.readObject();
            input.close();
            return um;
        } catch (IOException ex) {
            UserManager um = new UserManager();
            um.createOrganizer("chris", "pineapples123");
            um.createSpeaker("he", "haha");
            um.createOrganizer("stevens5", "Grapes64");
            um.createOrganizer("mbrian2", "hello2000");
            um.createAttendee("1roka", "TacoTuesday");
            um.createAttendee("hnguyen", "0BigMac0");
            um.createAttendee("msali70", "92!@f7");
            um.createAttendee("harper666", "0910");
            um.createAdminUser("Jonathan", "sus");
            um.createVIPUser("JonathanImposter", "sus");
            return um;
        }
    }

    /**
     * Stores the given UserManager object to the .ser file that is dedicated to storing the UserManager object that
     * the program uses.
     * @param usm the UserManager object that is to be saved
     * @throws IOException if an error occurs trying to save the data to the file
     */
    public void saveUM(UserManager usm) throws IOException {
        OutputStream file = new FileOutputStream("phase2/um.ser");
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        output.writeObject(usm);
        output.close();
    }

    /**
     * Returns the ConversationManager object that is stored in the file with the specified fileName. If no instance can
     * be found in such a file then a new ConversationManager object is returned.
     * @param fileName the file path that the method reads from
     * @return a ConversationManager object that is stored within the given fileName
     * @throws ClassNotFoundException if no ConversationManager object is found within fileName
     */
    public ConversationManager readCM(String fileName) throws ClassNotFoundException {

        try {
            InputStream file = new FileInputStream(fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            ConversationManager cm = (ConversationManager) input.readObject();
            input.close();
            return cm;
        } catch (IOException ex) {
            return new ConversationManager();
        }
    }

    /**
     * Stores the given ConversationManager object to the .ser file that is dedicated to storing the
     * ConversationManager object that the program uses.
     * @param cm the ConversationManager object that is to be saved
     * @throws IOException if an error occurs trying to save the data to the file
     */
    public void saveCM(ConversationManager cm) throws IOException {
        OutputStream file = new FileOutputStream("phase2/cm.ser");
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        output.writeObject(cm);
        output.close();
    }

    /**
     * Returns the EventManager object that is stored in the file with the specified fileName. If no instance can
     * be found in such a file then a new EventManager object is returned.
     * @param fileName the file path that the method reads from
     * @return an EventManager object that is stored within the given fileName
     * @throws ClassNotFoundException if an EventManager object cannot be found in fileName
     */
    public EventManager readEM(String fileName) throws ClassNotFoundException {

        try {
            InputStream file = new FileInputStream(fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            EventManager em = (EventManager) input.readObject();
            input.close();
            return em;
        } catch (IOException ex) {
            return new EventManager();
        }
    }

    /**
     * Stores the given EventManager object to the .ser file that is dedicated to storing the EventManager object that
     * the program uses.
     * @param em the EventManager object that is to be saved
     * @throws IOException if an error occurs trying to save the data to the file
     */
    public void saveEM(EventManager em) throws IOException {
        OutputStream file = new FileOutputStream("phase2/em.ser");
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        output.writeObject(em);
        output.close();
    }

//    public VIPEventManager readVM(String fileName) throws ClassNotFoundException {
//
//        try {
//            InputStream file = new FileInputStream(fileName);
//            InputStream buffer = new BufferedInputStream(file);
//            ObjectInput input = new ObjectInputStream(buffer);
//
//            VIPEventManager vm = (VIPEventManager) input.readObject();
//            input.close();
//            return vm;
//        } catch (IOException ex) {
//            return new VIPEventManager();
//        }
//    }
//
//    public void saveVM(VIPEventManager vm) throws IOException {
//        OutputStream file = new FileOutputStream("phase2/vm.ser");
//        OutputStream buffer = new BufferedOutputStream(file);
//        ObjectOutput output = new ObjectOutputStream(buffer);
//
//        output.writeObject(vm);
//        output.close();
//    }

}
