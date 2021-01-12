package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.*;
import UseCases.ConversationManager;
import UseCases.EventManager;
import UseCases.UserManager;

import java.util.*;

public class UserTracker extends Announcer{
    private final EventManager eventManager;

    public UserTracker(UserManager userManager, EventManager eventManager, ConversationManager convManager,
                       MainMenu mainMenu) {
        super(convManager, userManager, mainMenu);
        this.eventManager = eventManager;

    }

    /**
     * Asks the admin user whether to track event participants (Attendees, VIP users, Speakers)
     * or to view users who have already been marked as carriers of COVID
     */
    public void handleAdminTracker() {
        try {
            presenter.printRequest("Please enter 'track' to view all users or " +
                    "'marked' to go over marked COVID-carriers.");

            String decision = checkCommand(parser(), Arrays.asList("track", "marked"));
             if (decision.equals("track")) {
                 handleTrackUser();
             } else {handleMarkedUsers();}
        } catch(Exception e) {
            handleAdminTracker();
        }
    }

    /**
     * Allows the admin user to search for event participants by user ID
     * or they can view the entire list of event participants at the conference.
     * Admin users can select an event participant, which will call handleUserMark
     */
    public void handleTrackUser() {
        try {
            presenter.printRequest("Please enter 'search' to search for a user or press enter to select from all " +
                    "speakers, attendees, and VIP users.");
            String decision = checkCommand(parser(), Arrays.asList("search", ""));

            List<String> userList = new ArrayList<>();
            for (String userId : userManager.getAllUserIds()) {
                if (userManager.getUser(userId) instanceof EventParticipant) {
                    userList.add(userId);
                }
            }

            String userId;
            if (decision.equals("search")) {
                userId = handleSearchUser(userList);
            }
            else {
                presenter.printOptions(userList);
                presenter.printIndexRequest();
                int index = checkInt(parser(), userList.size());
                userId = userList.get(index);
            }
            handleUserMark(userId);

        } catch(Exception e) {
            handleTrackUser();
        }
    }

    /**
     * Allows the admin user to search for COVID carriers by userID
     * or they can view the entire list of known carriers at the conference.
     * Admin users can select an event participant, which will call handleUserMark
     */
    public void handleMarkedUsers() {
        try {
            if (userManager.getAllCovidCarriers().isEmpty()) {
                presenter.print("There are no users marked as COVID carriers.");
                handleAdminTracker();
            }
            presenter.printRequest("Please enter 'search' to search for a user or press enter to select from all " +
                    "speakers, attendees, and VIP users that are marked COVID-carriers");
            String decision = checkCommand(parser(), Arrays.asList("search", ""));

            List<String> userIds = new ArrayList<>();
            for (User user : userManager.getAllCovidCarriers()) {
                userIds.add(user.getUserId());
            }

            String userId;
            if (decision.equals("search")) {
                userId = handleSearchUser(userIds);
            }
            else {
                presenter.printOptions(userIds);
                presenter.printIndexRequest();
                int index = checkInt(parser(), userIds.size());
                userId = userIds.get(index);
            }
            handleUserMark(userId);
        } catch(Exception e) {
            handleMarkedUsers();
        }
    }

    /**
     * Allows the admin user to mark an event participant as a COVID carrier
     * or, if the selected user is a known carrier, can unmark the user.
     * When the admin user marks a user, covidMarkUpdate will be called
     * When the admin user unmarks a carrier, covidUnmarkUpdate will be called
     */
    public void handleUserMark(String userId) {
        try {
            if(!userManager.getUser(userId).getCovidStatus()) {
                presenter.printTracker(getAllAffectedEvents(userId), userManager.getUser(userId).getCovidStatus());
                presenter.print("Would you like to mark " + userId + " as a COVID carrier?");
                presenter.printYesNoRequest();

                String decision = checkCommand(parser(), Arrays.asList("yes", "no"));
                if (decision.equals("yes")) {
                    userManager.getUser(userId).setCovidStatus(true);
                    covidMarkUpdate(userId);
                    presenter.print("All users in proximity to the COVID carrier have been notified.");
                    handleAdminTracker();
                }
                handleAdminTracker();
            } else {
                presenter.print("Would you like to unmark " + userId + " as a COVID carrier?");
                presenter.printYesNoRequest();

                String decision = checkCommand(parser(), Arrays.asList("yes", "no"));
                if (decision.equals("yes")) {
                    userManager.getUser(userId).setCovidStatus(false);
                    covidUnmarkUpdate(userId);
                    presenter.print("Unmarked user has been notified.");
                    handleAdminTracker();
                }
                handleTrackUser();
            }
        }catch(Exception e) {
            handleUserMark(userId);
        }
    }

    public List<Event> getAllAffectedEvents(String userId) {
        List<Event> events = new ArrayList<>();
        for (UUID eventId : ((EventParticipant) userManager.getUser(userId)).getParticipantEvents()) {
            events.add(eventManager.getEvent(eventId));
        }
        return events;
    }

    /**
     * This removes the participant from all events and also prevents the user from signing up or speaking
     * at any events until further notice.
     * Sends a message to the marked user as well as a warning message to all event participants that were in
     * the same events as the COVID carrier.
     * @param userId ID of the selected user
     */
    public void covidMarkUpdate(String userId) {
        List<String> affectedUserIds = new ArrayList<>();
        for (Event event : getAllAffectedEvents(userId)) {
            User user = userManager.getUser(userId);
            if (!user.isSpeaker()) {
                eventManager.removeUser((Attendee) user, event);
            }
            else {
                eventManager.removeSpeaker((Speaker) user, event);
            }
            affectedUserIds.addAll(event.getAttendeeIds());
            affectedUserIds.addAll(event.getSpeakerIds());
            affectedUserIds = new ArrayList<>(new HashSet<>(affectedUserIds));
        }
        chooseOneUser(userId);
        sendToOne("Please be informed that you are marked for COVID. You are not allowed to sign up for events or speak at " +
                "events.");
        affectedUserIds.remove(userId);
        sendToAll(
                "A user that was attending the same event as you has been marked as a carrier for COVID. " +
                        "Be vigilant of any symptoms that arise.", affectedUserIds);

    }

    /**
     * This sends a message to the participant informing the user that they are no longer marked as a carrier
     * for COVID.
     * @param userId ID of the selected user
     */
    public void covidUnmarkUpdate(String userId) {
        chooseOneUser(userId);
        sendToOne("Please be informed that you are no longer marked for COVID. You are now allowed to sign up for events or speak at " +
                "events.");
    }
}
