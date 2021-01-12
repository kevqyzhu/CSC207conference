package UI;

import Entities.Event;
import Entities.Conversation;
import Entities.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterfacePresenter {

    /**
     * Prints an empty line for spacing purposes.
     */
    private void printSpacer() {
        System.out.println("");
    }

    /**
     * Prints a symbol to indicate that the program wants user input.
     */
    public void printInput() {
        System.out.print("> ");
    }

    /*
     * General requests
     */

    /**
     * Prints a given String to the screen.
     * @param message a String containing the message to be printed
     */
    public void print(String message) {
        printSpacer();
        System.out.println(message);
    }

    public void printRequest(String message) {
        printSpacer();
        System.out.println(message);
        printInput();
    }

    public void printYesNoRequest() {
        printRequest("Please enter 'yes' or 'no'.");
    }


    /**
     * Given a list of strings, prints each option to the screen along with a number to represent it.
     * @param options a list of strings to be printed to the client
     */
    public void printOptions(List<String> options) {
        printSpacer();
        int counter = 0;
        if (!options.isEmpty()) {
            for (String option : options) {
                System.out.println(counter++ + ": " + option);
            }
        }
    }

    /**
     * Given a list of strings, prints each option to the screen.
     * @param list a list of options to be printed to the client
     */
    public void printList(List<String> list) {
        printSpacer();
        if (!list.isEmpty()) {
            for (String option : list) {
                System.out.println(option);
            }
        }
    }

    /**
     * Prints a request asking the user to select an index from a given collection of indexes.
     */
    public void printIndexRequest() {
        printSpacer();
        System.out.println("Please enter the index of the selection you wish to make.");
        System.out.println("The index is specified by the leftmost number in each row above.");
        printInput();
    }

    /**
     * Prints the help menu to the screen.
     */
    public void printHelp() {
        printSpacer();
        System.out.println("Please follow the commands given before the input very carefully.");
        System.out.println("Reminder that you can enter \'exit\' anywhere to exit back to the main menu. " +
                "Enter \'quit\' to logout.");
    }

    /**
     * Prints a message to the screen to indicate the user has exited to the main menu.
     */
    public void printExit() {
        printSpacer();
        System.out.println("You have exited to the main menu.");
    }

    /**
     * Prints a message notifying the user that the program has exited.
     */
    public void printTerminate() {
        printSpacer();
        System.out.println("You have exited the program. All data has been saved.");
    }

    /*
     * Log In requests
     */

    /**
     * Prints the help menu to the screen when the user is on the login screen.
     */
    public void printLoginHelp() {
        printSpacer();
        System.out.println("Please follow the commands given before the input very carefully.");
        System.out.println("Enter \'quit\' if you want to exit the program.");
    }

    /**
     * Prints a request asking the user to input their username.
     */
    public void printUsernameRequest() {
        printSpacer();
        System.out.println("Please enter your username.");
        printInput();
    }

    /**
     * Prints a request asking the user to input their password.
     */
    public void printPasswordRequest() {
        printSpacer();
        System.out.println("Please enter your password.");
        printInput();
    }

    /**
     * Prints a small tip for the user to consider.
     */
    public void printTip() {
        System.out.println("Reminder that you can enter \'help\' anytime, anywhere.");
        printSpacer();
    }

    /*
     * Main menu requests
     */
    /**
     * Prints the main menu header.
     */
    public void printMenuRequest() {
        printSpacer();
        printTip();
        System.out.println("=== Main Menu ===");
    }

    /*
     * Event requests
     */
    /**
     * Print the events that have been scheduled to the screen with a number representing each event.
     * @param schedule an ArrayList of events that have been scheduled
     */
    public void printSchedule(List<Event> schedule) {
        printSpacer();
        int counter = 0;
        for (Event event : schedule) {
            System.out.println(counter++ + ": " + event);
        }
    }

    /**
     * Print the events that have been scheduled to the screen.
     * @param schedule an ArrayList of events that have been scheduled
     */
    public void printScheduleNoCounter(List<Event> schedule) {
        printSpacer();
        for (Event event : schedule) {
            System.out.println(event);
        }
    }

    /**
     * Prints a request to the screen asking the user whether they want to sign up to an event or cancel their
     * enrollment in one.
     * @param schedule an ArrayList of events that are scheduled
     */
    public void printEventSignupMenu(ArrayList<Event> schedule) {
        printSpacer();
        System.out.println("Here is your current schedule:");
        printScheduleNoCounter(schedule);
        System.out.println("\nPlease enter \'signup\' to signup for a new event or \'cancel\' to cancel enrollment in an event.");
        printInput();
    }


    /**
     * Print a message notifying the user their schedule is empty.
     */
    public void printNoSchedule() {
        printSpacer();
        System.out.println("You are not enrolled in any events. Redirecting you to signup for an event...");
    }

    /**
     * Print a message notifying the user there are no available events to enroll in.
     */
    public void printNoEvents() {
        printSpacer();
        System.out.println("There aren't any events you can enroll in. Redirecting you to the main menu...");
    }

    /*
     * Organizer Event requests
     */

    /**
     * Prints a request asking what date the event should be held on.
     */
    public void printDateOption() {
        printSpacer();
        System.out.println("Please choose which conference date to hold the event on.");
    }

    /**
     * Prints a request asking which room the event should be held in.
     */
    public void printRoomOption() {
        printSpacer();
        System.out.println("Please choose which room to hold the event in.");
    }

    /*
     * ConferenceControllers.ConferenceControllers.Messenger requests
     */

    /**
     * Print the list of conversations to the screen, and request the user to either respond to an existing conversation
     * or compose a new conversation.
     * @param conversations a list of Conversations a user has
     */
    public void printMessenger(List<String> conversations, ArrayList<String> decision) {
        printList(conversations);
        if(decision.size() == 4){
            System.out.println("\nPlease enter '"+ decision.get(0) +"' to respond to a message or '"+decision.get(1)+
                    "' to start a new conversation." +
                    "\nEnter '"+decision.get(2)+"' to mark a conversation as unread or '"+decision.get(3)+
                    "' to archive a conversation.");
        }
        else{
            System.out.println("\nPlease enter '"+ decision.get(0) +"' to respond to a message or '"+decision.get(1)+
                    "' to start a new conversation." +
                    "\nEnter '"+decision.get(2)+"' to mark a conversation as unread or '"+decision.get(3)+
                    "' to archive a conversation." +
                    "\nEnter '"+decision.get(4)+ "' to delete a conversation.");
        }

        printInput();
    }

    /**
     * Print the given conversation to the screen and request the user to either enter a message or exit to the main
     * menu.
     * @param conversation a Conversation object to be printed
     */
    public void printConversation(Conversation conversation) {
        printSpacer();
        if (!conversation.isEmpty()) { System.out.println(conversation.printConversation()); }
    }

    /**
     * Print the given conversation to the screen and request the user to either enter a message or exit to the main
     * menu.
     * @param conversation a Conversation object to be printed
     */
    public void printConversationResponse(Conversation conversation) {
        printConversation(conversation);
        printSpacer();
        System.out.println("Please enter your message or enter 'exit' to return to the main menu.");
        printInput();
    }

    /**
     * Prints a request asking the user for the contents of the message they want to send.
     */
    public void printSendMessage() {
        printSpacer();
        System.out.println("Please enter the message you want to send.");
        printInput();
    }

    /*
     * Speaker ConferenceControllers.ConferenceControllers.Messenger requests
     */
    /**
     * Prints the list of talks a speaker is given to the screen.
     * @param events a list of Events the speaker is speaking at
     */
    public void printTalks(List<Event> events) {
        printSpacer();
        System.out.println("You are speaking at these events:");
        if (!events.isEmpty()) {
            int counter = 0;
            for (Event event : events) {
                System.out.println(counter++ + ": " + event);
            }
        }
        printIndexRequest();
    }

    /*
     * Tracker requests
     */

    public void printTracker(List<Event> events, boolean covidStatus) {
        if (!events.isEmpty()) {
            print("Here are the events the user was involved in:");
            printScheduleNoCounter(events);
        }
        else {
            print("This user has not enrolled in any events.");
        }
        print("COVID Status: " + covidStatus);
    }

    public void printCovidSpeakerWarning(List<String> covidCarriers) {
        String last = covidCarriers.remove(covidCarriers.size()-1);
        if(covidCarriers.size() == 0) {
            print(last + " has been marked by an admin user" +
                    " to be a carrier of COVID and will therefore not be permitted to speak at any events.");
        }
        else if(covidCarriers.size() == 1) {
            print(String.join(", ", covidCarriers) + " and " + last + " have been marked by admin users" +
                    " to be carriers of COVID and will therefore not be permitted to speak at any events.");
        }
        else {
            print(String.join(", ", covidCarriers) + ", and " + last + " have been marked by admin users" +
                    " to be carriers of COVID and will therefore not be permitted to speak at any events.");
        }
    }

    /*
     * RequestHandler requests
     */

    public void printAttendeeViewRequest(List<String> pending, List<String> addressed) {
        printSpacer();
        System.out.println("== Pending Requests ==");
        if (pending.isEmpty()) {
            System.out.println("You have no pending requests.");
        }
        else {
            for (String request: pending) {
                System.out.println(request);
            }
        }
        printSpacer();
        System.out.println("== Addressed Requests ==");
        if (addressed.isEmpty()) {
            System.out.println("You have no addressed requests.");
        }
        else {
            for (String request: addressed) {
                System.out.println(request);
            }
        }
    }

    public void printOrganizerViewAddressed(ArrayList<String> requests) {
        printSpacer();
        if (requests.isEmpty()) {
            System.out.println("No user requests have been addressed.");
        }
        else {
            System.out.println("Requests that have been addressed:");
            printSpacer();
            for (String request: requests){
                System.out.println(request);
            }
        }
    }

    public void printPendingRequest() {
        printSpacer();
        System.out.println("Please enter the index of the request you would like to address.");
        System.out.println("If you do not want to address any of these, please type 'exit'.");
        System.out.println("The index is specified by the leftmost number in each row above.");
        printInput();
    }

    /*
     * Error printer
     */
    /**
     * Prints a message to the screen notifying the user that an error has occurred with their input.
     * @param errorMsg a String containing the details of the error
     */
    public void printError(String errorMsg) {
        printSpacer();
        System.out.println("There was an error reading your input: " + errorMsg + ". Please try again.");
    }

    /**
     * Prints a message to the screen notifying the user that their input was successful.
     * @param successMsg a String detailing what was successful
     */
    public void printSuccess(String successMsg) {
        printSpacer();
        System.out.println(successMsg);
    }
}