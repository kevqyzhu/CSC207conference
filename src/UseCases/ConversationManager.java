package UseCases;

import Entities.Conversation;
import Entities.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConversationManager implements Serializable {
    private final HashMap<UUID, Conversation> allConversations;
    private Conversation currentConversation;

    public ConversationManager() {
        allConversations = new HashMap<>();
    }

    /**
     * creates a conversation between 2 users
     * @param sender A User object representing the user sending a message
     * @param receiver A User object representing the user receiving a message
     * @return Returns a conversation object that represents a conversation between the sender and receiver users
     */
    public Conversation createConversation(User sender, User receiver) {
        Conversation newConversation = new Conversation(sender.getUserId(), receiver.getUserId());
        allConversations.put(newConversation.getConversationId(), newConversation);
        sender.addConversation(newConversation.getConversationId());
        receiver.addConversation(newConversation.getConversationId());
        return newConversation;
    }

    /**
     * opens a conversations between 2 users based on ID
     * @param conversationId A UUID that represents a unique conversation object
     * @return returns the Conversation object that is represented by the UUID
     */
    public Conversation openCurrentConversation(UUID conversationId) {
        currentConversation = getConversationById(conversationId);
        return currentConversation;
    }

    /**
     * opens current conversations based on sender and receiver
     * @param sender A User object representing the user sending a message
     * @param receiver A User object representing the user receiving a message
     * @return Returns a conversation object that represents a conversation between the sender and receiver users
     */
    public Conversation openCurrentConversation(User sender, User receiver) {
        if (!sender.getConversationIds().isEmpty()) {
            for (UUID convId : sender.getConversationIds()) {
                if (receiver.getConversationIds().contains(convId)) {
                    currentConversation = getConversationById(convId);
                    return currentConversation;
                }
            }
        }
        currentConversation = createConversation(sender, receiver);
        return currentConversation;
    }

    /**
     * updates a conversation with new text for a user
     * @param currentUser A user object representing the user you wish to update the current conversation of
     * @param text a String representing the desired text you wish to send
     */
    public void updateCurrentConversation(User currentUser, String text) {
        ArrayList<Object> message = new ArrayList <>();
        message.add(currentUser.getUserId());
        message.add(text);
        currentConversation.addMessage(LocalDateTime.now(), message);
    }

    public void deleteConversation(User sender, User receiver){
        allConversations.remove(currentConversation.getConversationId());
        sender.deleteConversation(currentConversation.getConversationId());
        receiver.deleteConversation(currentConversation.getConversationId());
        closeCurrentConversation();
    }

    public void deleteUserConversation(String currentUserId){
        currentConversation.deleteMessage(currentUserId);
        // currentConversation.deleteAllMessage();
    }

    /**
     * closes the current conversation
     */
    public void closeCurrentConversation() {
        currentConversation = null;
    }

    /**
     * Get all conversations a user has
     * @param usr A user object representing the user you want to see the conversations of
     * @return Returns a list of Conversation objects that represents all the conversations that user has
     */
    public List<Conversation> getConversationsByUser(User usr) {
        List<Conversation> conversations = new ArrayList<>();
        if (!usr.getConversationIds().isEmpty()) {
            for (UUID convId : usr.getConversationIds()) {
                conversations.add(getConversationById(convId));
            }
        }
        return conversations;
    }

    public List<Conversation> getArchivedConversationsByUser(User usr) {
        List<Conversation> conversations = new ArrayList<>();
        if (!usr.getArchivedConversationIds().isEmpty()) {
            for (UUID convId : usr.getArchivedConversationIds()) {
                conversations.add(getConversationById(convId));
            }
        }
        return conversations;
    }

    /**
     * Get a conversation a user has by its id
     * @param conversationId A UUID that represents a unique conversation object
     * @return Returns a conversation object
     */
    public Conversation getConversationById(UUID conversationId) {
        return allConversations.get(conversationId);
    }
}
