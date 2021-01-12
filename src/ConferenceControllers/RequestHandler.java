package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.Attendee;
import UseCases.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RequestHandler extends ConferenceController {
    private final UserManager userManager;

    public RequestHandler(UserManager userManager, MainMenu mainMenu){
        super(mainMenu);
        this.userManager = userManager;
    }

    /**
     * Requests the Attendee to decide whether they want to create a request or view a request. Based on their input,
     * it will direct them to the appropriate menu.
     */
    public void handleAttendeeRequestOptions() {
        try {
            List<String> attendeeOptions = Arrays.asList("create", "view");
            // presenter.printOptions(attendeeOptions);
            presenter.printRequest("Please enter 'create' to create a new request or 'view' to view your current" +
                    " requests.");

            String input = parser();
            String decision = checkCommand(input, attendeeOptions);
            if (decision.equals("create")) {
                handleAttendeeCreateRequest();
            }
            else if (decision.equals("view")) {
                handleAttendeeViewRequest();
            }
        }
        catch(Exception e) {
            handleAttendeeRequestOptions();
        }
    }

    /**
     * Gets the list of pending and addressed requests of the Attendee that is logged in and calls the presenter to
     * print them to the screen.
     */
    public void handleAttendeeViewRequest() {
        String id = userManager.getCurrentUser().getUserId();
        Attendee usr = userManager.getAttendee(id);
        presenter.printAttendeeViewRequest(usr.getPendingRequests(), usr.getAddressedRequests());

        handleAttendeeRequestOptions();
    }

    /**
     * Asks the Attendee to input the request they want to make and adds it to their list of pending requests.
     */
    public void handleAttendeeCreateRequest() {
        try{
            String id = userManager.getCurrentUser().getUserId();
            Attendee usr = userManager.getAttendee(id);

            presenter.printRequest("Please enter the request you want to make.");
            String input = parser();
            usr.addRequest(input);
            presenter.printSuccess("You have successfully created a new request.");
            handleAttendeeRequestOptions();
        }
        catch(Exception e) {
            presenter.printError("Unable to create your request");
            handleAttendeeCreateRequest();
        }
    }

    /**
     * Asks the Organizer if they want to view all pending user requests or view all addressed user requests. Depending
     * on the input, it will direct them to the appropriate menu.
     */
    public void handleOrganizerRequestOptions() {
        try {
            List<String> organizerOptions = Arrays.asList("pending", "addressed");
            // presenter.printOptions(organizerOptions);
            presenter.printRequest("Please enter 'pending' to view pending user requests or 'addressed' to view addressed" +
                    "user requests.");

            String input = parser();
            String decision = checkCommand(input, organizerOptions);
            if (decision.equals("pending")) {
                handleOrganizerViewPending();
            }
            else if (decision.equals("addressed")) {
                handleOrganizerViewAddressed();
            }
        }
        catch(Exception e) {
            handleOrganizerRequestOptions();
        }
    }

    /**
     * Gets all of the pending requests that users in the program have. If there are pending requests in the program,
     * asks the Organizer if they want to search for requests or choose from the entire list. The requests will be
     * printed to the screen through the presenter and the Organizer is asked to choose if they want to address a
     * request. If they choose a request, move the request from the appropriate Attendee's pending requests to their
     * addressed requests.
     */
    public void handleOrganizerViewPending() {
        try {
            ArrayList<String> organizerOptions = new ArrayList<>();
            Map<String, ArrayList<String>> pendingRequests = userManager.getAllPendingRequests();
            for (Map.Entry<String, ArrayList<String>> entry : pendingRequests.entrySet()) {
                for (String request : entry.getValue()) {
                    organizerOptions.add(entry.getKey() + ": " + request);
                }
            }
            if (organizerOptions.isEmpty()){
                presenter.print("No user requests have been made.");
            }
            else {
                presenter.printRequest(
                        "Please enter 'search' to look for a request. Press enter to choose from the entire list.");

                String decision = checkCommand(parser(), Arrays.asList("search", ""));
                if (decision.equals("search")) {
                    handleRequestSearch(organizerOptions);
                }
                presenter.printOptions(organizerOptions);
                presenter.printPendingRequest();

                String input = parser();
                int index = checkInt(input, organizerOptions.size());
                String[] details = organizerOptions.get(index).split(": ");
                Attendee attendee = userManager.getAttendee(details[0]);
                attendee.removeRequest(details[1]);
                presenter.printSuccess("You have successfully addressed this request.");
            }
            handleOrganizerRequestOptions();

        }
        catch(Exception e) {
            handleOrganizerViewPending();
        }
    }

    /**
     * Takes in a list of pending requests and prompts the Organizer to type in a keyword to search requests by. Shows
     * the Organizer the list of pending requests that matched their search, and prompts them to choose a request to
     * address. If a request is chosen, move it from the appropriate Attendee's pending requests to their addressed
     * requests.
     * @param organizerOptions the list of all pending requests that all users currently have
     */
    public void handleRequestSearch(List<String> organizerOptions) {
        try {
            ArrayList<String> options = (ArrayList<String>) handleSearch(organizerOptions);
            presenter.printIndexRequest();
            int index = checkInt(parser(), options.size());

            String[] details = organizerOptions.get(index).split(": ");
            Attendee attendee = userManager.getAttendee(details[0]);
            attendee.removeRequest(details[1]);
            presenter.printSuccess("You have successfully addressed this request.");
            handleOrganizerRequestOptions();

        } catch (Exception e) {
            handleRequestSearch(organizerOptions);
        }
    }

    /**
     * Gets all the addressed requests that the users in the program have, and calls the presenter to print them to
     * the screen.
     */
    public void handleOrganizerViewAddressed() {
        ArrayList<String> requestList = new ArrayList<>();
        Map<String, ArrayList<String>> addressedRequests = userManager.getAllAddressedRequests();
        for (Map.Entry<String, ArrayList<String>> entry : addressedRequests.entrySet()) {
            for (String request : entry.getValue()) {
                requestList.add(entry.getKey() + " - " + request);
            }
        }
        presenter.printOrganizerViewAddressed(requestList);

        handleOrganizerRequestOptions();
    }


}
