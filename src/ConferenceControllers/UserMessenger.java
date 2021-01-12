package ConferenceControllers;

import ConferenceControllers.MainMenu.MainMenu;
import Entities.Conversation;
import Entities.User;
import UseCases.ConversationManager;
import UseCases.UserManager;

import java.util.*;

public class UserMessenger extends ConferenceController{
    private final ConversationManager convManager;
    private final UserManager userManager;
    private Decision decision;

    /**
     * @param convManager a ConversationManager object that is passed in so this object can use it
     * @param userManager a UserManager object that is passed in so this object can use it
     * @param mainMenu this program's main menu
     */
    public UserMessenger(ConversationManager convManager, UserManager userManager, MainMenu mainMenu, Decision decision) {
        super(mainMenu);
        this.convManager = convManager;
        this.userManager = userManager;
        setDecision (decision);
    }

    /**
     * gets a list of contacts the current user has
     * @return returns a list of strings that represents the IDs of each person in the current users contacts
     */
    public List<String> getContacts() {
        return userManager.getCurrentUser().getContacts();
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    /**
     *  Will call the UserManager.isValidId and getCurrentUser methods to add a contact based on passed in data.
     *         Will return true if its able to add a contact. Returns false if a contact with that name already exists.
     * @param contactId a String that represents the id of the current user's list of contacts
     * @return returns a Boolean value where its true if a contact was added successfully, false otherwise
     */
    public boolean addContact(String contactId) {
        if (userManager.isValidId(contactId) && userManager.getCurrentUser() != null) {
            userManager.getCurrentUser().addContact(contactId);
            return true;
        }
        return false;
    }

    private void handleMessengerDelete(int index) {
        try{
            Conversation conversation = getConversationByIndex(index);
            convManager.deleteConversation(userManager.getUser(conversation.getUser1Id()), userManager.getUser(conversation.getUser2Id()));
            presenter.printSuccess("Your conversation has been deleted successfully.");
            handleMessenger();
        }catch (Exception e) {
            handleMessengerDelete(index);
        }
    }

    /**
     * gets a list of conversations for the current user
     * @return returns a list of Conversation objects that represents the current user's conversations
     */
    public List<Conversation> getConversations() {
        return convManager.getConversationsByUser(userManager.getCurrentUser());
    }

    /**
     * gets a list of archived conversations for the current user
     * @return returns a list of Conversation objects that represents the current user's archived conversations
     */
    public List<Conversation> getArchivedConversations() {
        return convManager.getArchivedConversationsByUser(userManager.getCurrentUser());
    }

    /**
     * gets list of conversations a user has and creates a new list called ConversationStrings
     * iterates through list and converts conversations between current user and second user into strings as part of list
     * @return list of conversationStrings
     */
    public List<String> getConversationsString() {
        List<Conversation> conversations = getConversations();
        List<String> conversationStrings = new ArrayList<>();
        for (Conversation conversation : conversations) {
            if (conversation.getLatestSenderId().equals(this.userManager.getCurrentUser().getUserId())) {
                conversation.markAsRead(conversation.getLatestSenderId());
            }
            if (conversation.getUser1Id().equals(userManager.getCurrentUser().getUserId())) {
                conversationStrings.add(conversation.getUser2Id() + conversation.toString() + "   [" + conversation.getReadStatus(this.userManager.getCurrentUser().getUserId()) + "]");
            } else {
                conversationStrings.add(conversation.getUser1Id() + conversation.toString() + "   [" + conversation.getReadStatus(conversation.getUser2Id()) + "]");
            }
        }
        return conversationStrings;
    }

    /**
     * gets list of archived conversations a user has and creates a new list called ConversationStrings
     * iterates through list and converts archived conversations between current user and second user into strings as part of list
     * @return list of conversationStrings
     */
    public List<String> getArchivedConversationsString() {
        List<Conversation> conversations = getArchivedConversations();
        List<String> conversationStrings = new ArrayList<>();
        for (Conversation conversation : conversations) {
            if (conversation.getLatestSenderId().equals(this.userManager.getCurrentUser().getUserId())) {
                conversation.markAsRead(conversation.getLatestSenderId());
            }
            if (conversation.getUser1Id().equals(userManager.getCurrentUser().getUserId())) {
                conversationStrings.add(conversation.getUser2Id() + conversation.toString() + "   [" + conversation.getReadStatus(this.userManager.getCurrentUser().getUserId()) + "]");
            } else {
                conversationStrings.add(conversation.getUser1Id() + conversation.toString() + "   [" + conversation.getReadStatus(conversation.getUser2Id()) + "]");
            }
        }
        return conversationStrings;
    }

    /**
     *  gets the number of conversations the current user is having
     * @return returns an integer that represents the amount of conversations a user is having
     */
    public int getConversationAmount() {
        return convManager.getConversationsByUser(userManager.getCurrentUser()).size();
    }

    /**
     * Creates a list of conversations using getConversations method and uses the openCurrentConversation method from
     *         convManager to return the conversation based on the ID provided by the passed in index data.
     * @param conversationIndex an Integer that represents the index of the conversation
     * @return returns a conversation object that represents the conversation of a current user found based on the
     * conversation index
     */
    public Conversation getConversationByIndex(int conversationIndex) {
        List<Conversation> conversations = getConversations();
        UUID conversationId = conversations.get(conversationIndex).getConversationId();
        return convManager.openCurrentConversation(conversationId);
    }

    /**
     *  gets a conversation by using a user's index to find it
     * @param userIndex an Integer that represents the current user's index
     * @return returns a conversation object that represents the conversation of a current user found based on the
     * user index
     */
    public Conversation getConversationByUser(int userIndex) {
        String receiverId = userManager.getCurrentUser().getContacts().get(userIndex);
        User receiver = userManager.getUser(receiverId);
        return convManager.openCurrentConversation(userManager.getCurrentUser(), receiver);
    }

    /**
     * updates the current conversation with a new string
     * @param message a String that represents a new message
     */
    public void updateConversation(String message) {
        convManager.updateCurrentConversation(userManager.getCurrentUser(), message);
    }

    /**
     * closes the current conversation
     */
    public void closeConversation() {
        convManager.closeCurrentConversation();
    }

    /**
     * handles the messages
     * composes the messages
     * now the presenter of messenger shows the messages
     * message is inputted
     * exceptions are handled
     * after that there 2 options respond or compose
     * if send then compose
     * else respond
     */
    public void handleMessenger() {
        try {
            List<Conversation> conversations = getConversations();
            if (conversations.isEmpty()) {
                presenter.print("You do not have any conversations. Redirecting you to compose one...");
                handleMessengerCompose();
            }
            ArrayList<String> decision1;
            decision1 = this.decision.decision();
            presenter.printMessenger(getConversationsString(), decision1);

            String input = parser();

            String decision = checkCommand(input, decision1);
            if (decision.equals("respond")) { handleMessengerRespond(decision); }
            else if (decision.equals("compose")) { handleMessengerCompose(); }
            else if (decision.equals("mark as unread")) { handleMessengerMarkAsUnread(); }
            else if (decision.equals("archive")) { handleMessengerArchive(); }
            else if (decision.equals("delete")) { handleMessengerRespond("delete"); }

        } catch(Exception e) {
            handleMessenger();
        }
    }

    /**
     *Handles user input for responding to messages
     */
    private void handleMessengerRespond(String des) {
        try {
            presenter.printOptions(getConversationsString());
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, getConversationAmount());// delete from here

            if (des.equals("respond")) {
                handleMessengerRespondReply(index);
            } else if (des.equals("delete")){
                handleMessengerDelete(index);
            }
        } catch(Exception e) {
            handleMessengerRespond(des);
        }
    }

    /**
     * current Conversation is displayed based on index passed in
     * based on the String input parsed, Conversation is updated
     * Conversation is then closed and returned to main menu
     * @param index the index chosen by the user
     */
    private void handleMessengerRespondReply(int index) {
        try {
            presenter.printConversationResponse(getConversationByIndex(index));
            //the following single line marks the convo as read by the person who is about to respond to it
            getConversationByIndex(index).markAsRead(this.userManager.getCurrentUser().getUserId());

            String input = parser();
            updateConversation(input);
            closeConversation();
            mainMenu.handleMainMenu();
        } catch(Exception e) {
            handleMessengerRespondReply(index);
        }
    }

    /**
     * list of contacts is displayed
     * user is provided with options to choose a contact or add one (only shown "add" if there are no contacts)
     * user input is then parsed and calls appropriate method based on input
     */
    private void handleMessengerCompose() {
        try {
            List<String> contacts = getContacts();
            if (contacts.isEmpty()) {
                presenter.print("You do not have any contacts. Redirecting you to add a new one...");
                handleMessengerAdd();
            }
            presenter.printList(contacts);
            presenter.printRequest("Please enter 'choose' to message a recipient from your contacts listed above or 'add' to add a new contact.");

            String input = parser();
            String decision = checkCommand(input, Arrays.asList("choose", "add"));
            if (decision.equals("choose")) { handleMessengerChooseUser(); }
            else { handleMessengerAdd(); }
        } catch(Exception e) {
            handleMessengerCompose();
        }
    }



    /**
     * list of contacts is displayed along with index for each conversation
     * input of index is parsed and conversation is selected
     */
    private void handleMessengerChooseUser() {
        try {
            List<String> contacts = getContacts();
            presenter.printOptions(contacts);
            presenter.printIndexRequest();

            String input = parser();
            int index = checkInt(input, contacts.size());
            handleMessengerChooseUserRespond(index);

        } catch(Exception e) {
            handleMessengerChooseUser();
        }
    }

    /**
     * based on index, String input is parsed as message and conversation is updated
     * conversation is then closed
     * return to main menu
     * @param index the index chosen by the user
     */
    private void handleMessengerChooseUserRespond(int index) {
        try {
            presenter.printConversationResponse(getConversationByUser(index));

            String input = parser();
            updateConversation(input);
            closeConversation();
            mainMenu.handleMainMenu();
        } catch(Exception e) {
            handleMessengerChooseUserRespond(index);
        }
    }

    /**
     * if add is requested, String input with contact name is passed into addContact
     * if requested contact is at conference, confirmation of add is displayed
     * if not at conference, then error message displayed and user prompted to try adding again
     */
    private void handleMessengerAdd() {
        try {

            String input = handleSearchUser(userManager.getAllUserIds());

            if (addContact(input)) {
                presenter.printSuccess(input + " has been added to your contacts.");
                handleMessenger();
            }
            else {
                presenter.printError(input + " is not at the conference.");
                handleMessengerAdd();
            }
        } catch(Exception e) {
            handleMessengerAdd();
        }
    }

    private void handleMessengerMarkAsUnread() {
        try {
            this.presenter.printOptions(this.getConversationsString());
            this.presenter.print("Please select the conversation you'd like to mark as unread");
            this.presenter.printIndexRequest();
            String input = parser();
            int index = checkInt(input, this.getConversationsString().size());
            this.getConversationByIndex(index).markAsUnread(this.userManager.getCurrentUser().getUserId());
            presenter.print("Conversation has been marked unread.");
            this.handleMessenger();
        }
        catch(Exception e) {
            handleMessengerMarkAsUnread();
        }
    }

    private void handleMessengerArchive() {
        try {
            this.presenter.printOptions(this.getConversationsString());
            this.presenter.print("Please select the conversation you'd like to archive.");
            this.presenter.printIndexRequest();
            String input = parser();
            int index = checkInt(input, this.getConversationsString().size());
            this.userManager.getCurrentUser().archiveConversation(this.getConversationByIndex(index).getConversationId());
            presenter.print("Conversation has been archived.");
            mainMenu.handleMainMenu();
        }
        catch(Exception e) {
            handleMessengerArchive();
        }
    }

    /**
     * Displays options of viewing archived conversations
     * Displays list of currently archived conversations
     * Parses input index and calls getArchivedConversationsString and displays conversation
     * Prompts user to type 'exit' to go back to the main menu
     * @throws Exception
     */
    public void handleMessengerViewArchivedConversations() throws Exception {
        this.presenter.print("Here are your archived conversations:");
        this.presenter.printOptions(getArchivedConversationsString());
        this.presenter.print("Please select the archived conversation you'd like to view.");
        this.presenter.printIndexRequest();
        int index = checkInt(parser(), getArchivedConversationsString().size());
        presenter.printConversation(getArchivedConversations().get(index));
        this.presenter.print("Enter 'exit' to go back to the main menu.");
        presenter.printInput();
        parser();
        mainMenu.handleMainMenu();
    }
}
