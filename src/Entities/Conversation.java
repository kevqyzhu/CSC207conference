package Entities;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Conversation implements Serializable {
    private final UUID conversationId;
    private final String user1Id;
    private final String user2Id;
    private boolean user1Read = false;
    private boolean user2Read = false;

    /*
     * This stores the conversation history between two users. List contains the ID of the User who sent it and
     * the string of the message
     */
    private final HashMap<LocalDateTime, ArrayList<Object>> conversationHistory;

    public Conversation(String senderId, String receiverId) {
        this.user1Id = senderId;
        this.user2Id = receiverId;
        conversationId = UUID.randomUUID();
        conversationHistory = new HashMap<>();
    }

    /**
     * @return UUID: This conversation's UUID
     */
    public UUID getConversationId() {
        return this.conversationId;
    }

    /**
     * @return String: The ID of this conversation's first User
     */
    public String getUser1Id() {
        return this.user1Id;
    }

    /**
     * @return String: The ID of this conversation's second User
     */
    public String getUser2Id() {
        return this.user2Id;
    }

    /**
     * Adds a message to conversationHistory.
     * @param time: The time of the new message
     * @param message: The content of the new message
     */
    public void addMessage(LocalDateTime time, ArrayList<Object> message) {
        conversationHistory.put(time, message);
    }


    /**
     *
     * @return true iff the conversationHistory is empty
     */
    public boolean isEmpty() {
        return new TreeSet<>(conversationHistory.keySet()).isEmpty();
    }

    public void deleteMessage(String currentUserId){
        //conversationHistory.
        //any message?
        for(LocalDateTime date: conversationHistory.keySet()){
            ArrayList<Object> message = conversationHistory.get(date);
            if (message.get(0) == currentUserId){
                conversationHistory.remove(date, message);
            }
        }
    }

    public void deleteAllMessage(){
        conversationHistory.clear();
    }
    /**
     * Returns a string containing all messages
     * @return: String
     */
    public String printConversation() {
        StringBuilder messageHistory = new StringBuilder();
        SortedSet<LocalDateTime> dates = new TreeSet<>(conversationHistory.keySet());
        if (!dates.isEmpty()) {
            for (LocalDateTime date : dates) {
                ArrayList<Object> message = conversationHistory.get(date);
                messageHistory.append(message.get(0)).append(':').append(' ').append(message.get(1)).append('\n');
            }
        }
        return messageHistory.substring(0, messageHistory.length() -1);
    }

    /**
     * Sets user1Read or user2Read to true, depending on input user id
     */
    public void markAsRead(String userId) {
        if (userId.equals(user1Id)) {
            this.user1Read = true;
        }
        else if (userId.equals(user2Id)) {
            this.user2Read = true;
        }
    }
    /**
     * Sets user1Read or user2Read to false, depending on input user id
     */
    public void markAsUnread(String userId) {
        if (userId.equals(user1Id)) {
            this.user1Read = false;
        }
        else if (userId.equals(user2Id)) {
            this.user2Read = false;
        }
    }

    /**
     *
     * @return returns user1Read or user2Read depending on input
     */
    public String getReadStatus(String userId) {
        if (userId.equals(user1Id)) {
            if (this.user1Read) {
                return "read";
            }
            else {
                return "unread";
            }
        }
        else if (userId.equals(user2Id)) {
            if (this.user2Read) {
                return "read";
            }
            else {
                return "unread";
            }
        }
        return "unread";
    }

    /**
     *
     * @return string of sender ID
     */
    public String getLatestSenderId() {
        SortedSet<LocalDateTime> dates = new TreeSet<>(conversationHistory.keySet());
        LocalDateTime date = dates.last();
        return (String) conversationHistory.get(date).get(0);
    }

    /**
     * Creates a list of messages in conversation history based on latest date
     * Adds to list with message
     * @return contents of message in/and conversation in string format
     */
    @Override
    public String toString() {
        SortedSet<LocalDateTime> dates = new TreeSet<>(conversationHistory.keySet());
        LocalDateTime date = dates.last();
        ArrayList<Object> message = conversationHistory.get(date);
        String lastMessage = "";
        lastMessage = lastMessage + ':' + ' ' + message.get(1);
        return lastMessage;
    }
}

