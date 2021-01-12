package UseCases;

import Entities.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class UserManager implements Serializable {
    //Creates the list of all users
    private final List<User> allUsers;
    private final List<String> allUserID = new ArrayList<String>();
    // set this by Controllers.LoginReceiver
    private User currentUser;
    //Map <EventID, List<Integer>> usersByEventsId; // note I think it should be eventID and UserID

    /**
     * This is the constructor for the UserManager. This constructor will be called in the Controller
     * which need it. This instantiates allUsers which gives an list of Users
     */
    public UserManager() {
        allUsers = new ArrayList<>();
    }

    /**
     * Adds the user to the list of allUsers.
     * @param usr
     */
    public void addUser(User usr){
        allUsers.add(usr);
    }

    /**
     * Removes the user from the system. Takes in the usr param and the user is removed in the list allUsers.
     * The usr specifier is the user object which is to be removed from the list of users
     * @param usr
     */
    public void removeUser(User usr){
        allUsers.remove(usr);
    }

    /**
     * Returns a Speaker. The method create a speaker object and return it to be used by other controllers.
     * This is later used in the UI level. The userId is a the id of the speaker and the password is the speakers
     * password for when they want to login to the system
     * @param userId
     * @param password
     * @return: the new speaker object created by the method
     */

    public Speaker createSpeaker(String userId, String password) {
        //handle the case of userId already being in the allUserId list at the UI level
        Speaker newSpeaker = new Speaker(userId, password);
        allUsers.add(newSpeaker);
        allUserID.add(userId);
        return newSpeaker;

    }

    /**
     * Returns a Attendee. The method create an attendee object and return it to be used by other controllers.
     * This is later used in the UI level. The userId is a the id of the attendee and the password is the attendee´s
     * password for when they want to login to the system
     * @param userId
     * @param password
     * @return: The new Attendee object is returned
     */
    // following two methods are for instantiating organizers and attendees that the gateway class
    // will need
    public Attendee createAttendee(String userId, String password) {
        Attendee newAttendee = new Attendee(userId, password);
        allUsers.add(newAttendee);
        allUserID.add(userId);
        return newAttendee;
    }

    /**
     *  Returns an Organizer. The method create an organizer object and return it to be used by other controllers.
     *  This is later used in the UI level. The userId is a the id of the organizer and the password is the organizer´s
     *  password for when they want to login to the system
     * @param userId
     * @param password
     */
    public void createOrganizer(String userId, String password) {
        Organizer newOrganizer = new Organizer(userId, password);
        allUsers.add(newOrganizer);
        allUserID.add(userId);
    }

    public void createAdminUser(String userId, String password){
        AdminUser newAdminUser = new AdminUser(userId, password);
        allUsers.add(newAdminUser);
        allUserID.add(userId);
    }

    public VIPUser createVIPUser(String userId, String password){
        VIPUser newVIPUser = new VIPUser(userId, password);
        allUsers.add(newVIPUser);
        allUserID.add(userId);
        return newVIPUser;
    }

    /**
     * @param Id String the username unique to a user
     * @return boolean representing if the provided speakerId corresponds to an actual speaker object.
     */

    public boolean isUserType(String Id, int type) {
        for (User user : getAllUserType(type)) {
            if (Id.equals(user.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the current user.
     * @return
     */
    public User getCurrentUser() { return currentUser; }

    /**
     * @param usr
     */
    public void setCurrentUser(User usr) { currentUser = usr; }


   //not used atm
    //public List<String> getUsersByEvents(Event event){
    //   return event.getAttendeeIds();
    //}

    /**
     * Gets and returns a user given the user's userID
     * @param userID
     * @return
     */
    public User getUser(String userID){
        if (!allUsers.isEmpty()) {
            for (User user : allUsers) {
                if (user.getUserId().equals(userID)) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Returns the Attendee who's ID is attendeeID
     * @param attendeeID
     * @return
     */
    public Attendee getAttendee(String attendeeID){
        /*
        Returns the attendee object who's ID is attendeeID. Returns null otherwise.
         */
        return (Attendee) getUser(attendeeID);
    }

    public VIPUser getVIPUser(String VIPUserID){
        return (VIPUser) getUser(VIPUserID);
    }

    /**
     * Returns the speaker object specified by the speakerID passed in
     * @param speakerID
     * @return
     */
    public Speaker getSpeaker(String speakerID){
        /*
        Returns the speaker object who's ID is speakerID. Returns null otherwise.
         */
        return (Speaker) getUser(speakerID);
     }


    /**
     * Returns true if the provided user id and password is valid.
     * @param usrID
     * @param password
     * @return
     */
    // need this for Login Receiver: SD
    public boolean findUserId(String usrID, String password){
        for(User usr: allUsers){
            if (usr.getUserId().equals(usrID) && usr.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a list of all speakers in the system.
     * @return
     */
    public List<User> getAllUserType(int type) {
        ArrayList<User> users = new ArrayList<>();
        if (!allUsers.isEmpty()) {
            for (User user : allUsers) {
                if (type == 1) {
                    if (user.isOrganizer()) {
                        users.add((Organizer) user);
                    }
                } else if (type == 2) {
                    if (user.isSpeaker()) {
                        users.add((Speaker) user);
                    }
                } else if (type == 3) {
                    if (user.isAttendee()) {
                        users.add((Attendee) user);
                    }
                } else if (type == 4) {
                    if (user.isAdminUser()) {
                        users.add((AdminUser) user);
                    }
                } else if (type == 5) {
                    if (user.isVIPUser()) {
                        users.add((VIPUser) user);
                    }
                }
            }
        }
        return users;
    }

    /**
     * gets a list of users by specific type
     * @return returns a list of Ids that represent the Users
     */
    public List<String> getAllUserTypeIds(int type) {
        /*
        Returns a list containing all speaker ids
         */
        List<String> userIds = new ArrayList<>();
        for (User user : getAllUserType(type)) {
            userIds.add(user.getUserId());
        }
        return userIds;
    }

    public List<String> getAllUserIds() {
        return allUserID;
    }

    /**
     * returns a list of all attendees in the system
     * @return
     */

    /**
     * Checks if the provided userId is valid or not
     * @param usrID
     * @return
     */
    public boolean isValidId(String usrID) {
        for(User usr: allUsers){
            if (usr.getUserId().equals(usrID)){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all of the pending requests that all the Attendees in the program have made.
     * @return A map that has userId as the key and that attendee's list of pending requests as the corresponding value
     */
    public Map<String, ArrayList<String>> getAllPendingRequests() {
        Map<String, ArrayList<String>> pendingRequests = new HashMap<>();
        for (User usr: allUsers){
            if (usr.isAttendee()) {
                Attendee attendee = getAttendee(usr.getUserId());
                pendingRequests.put(attendee.getUserId(), (ArrayList<String>) attendee.getPendingRequests());
            }
        }
        return pendingRequests;
    }

    /**
     * Gets all of the addressed requests that all the Attendees in the program have made.
     * @return A map that has userId as the key and that attendee's list of addressed requests as the corresponding
     * value
     */
    public Map<String, ArrayList<String>> getAllAddressedRequests() {
        Map<String, ArrayList<String>> addressedRequests = new HashMap<>();
        for (User usr: allUsers){
            if (usr.isAttendee()) {
                Attendee attendee = getAttendee(usr.getUserId());
                addressedRequests.put(attendee.getUserId(), (ArrayList<String>) attendee.getAddressedRequests());
            }
        }
        return addressedRequests;
    }

    public List<User> getAllCovidCarriers() {
        List<User> users = new ArrayList<>();
        for (User user : allUsers ) {
            if (user.getCovidStatus()) {
                users.add(user);
            }
        }
        return users;
    }
}
