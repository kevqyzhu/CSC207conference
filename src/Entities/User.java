package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Change the privacy of variables as needed, just made them without considering what their privacy should be
public abstract class User implements Serializable {
    private final String userId;
    private final String password;
    private ArrayList<UUID> eventIds;
    private ArrayList<UUID> conversationIds;
    private final ArrayList<UUID> archivedConversationIds;
    private final ArrayList<String> contacts;
    private boolean hasCovid = false;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.eventIds = new ArrayList<>();
        this.conversationIds = new ArrayList<>();
        this.archivedConversationIds = new ArrayList<>();
        this.contacts = new ArrayList<>();
    }

    /**
     * Returns the User's userId
     * @return
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     *
     * @return the user's password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *
     * @return ArrayList<UUID>: List of event UUIDs for the events the user is attending
     */
    public List<UUID> getEventIds() {
        return this.eventIds;
    }

    /**
     * Sets the list of event Ids
     * @param eventIds The new list of event Ids.
     */
    public void setEventIds(ArrayList<UUID> eventIds) {
        this.eventIds = eventIds;
    }

    /**
     * Adds an event to the user's list of events
     * @param eventId: UUID of the event
     * @return boolean indicating if the action was successful
     */
    public boolean addEventId(UUID eventId) {
        /*
        If eventId is not already in eventIds, then add eventId and return true. Otherwise, do nothing and return false.
         */
        if (!this.eventIds.contains(eventId)) {
            this.eventIds.add(eventId);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Removes an event id from the list of event IDs
     * @param eventId : The event ID to be removed
     */
    public void removeEventId(UUID eventId) {
        /*
        If eventId is in eventIds, remove it and return true. Otherwise, do nothing and return false.
         */
        this.eventIds.remove(eventId);
    }

    /**
     * Returns true if the user has a certain conversation stored
     * @param conversationId: The conversation to check
     * @return boolean indicating if the user has this conversation or not
     */
    public boolean hasConversation(UUID conversationId) { return conversationIds.contains(conversationId); }

    /**
     *
     * @return list of conversation IDs
     */


    public List<UUID> getConversationIds() {
        return new ArrayList<>(this.conversationIds);
    }

    public List<UUID> getArchivedConversationIds() {
        return new ArrayList<>(this.archivedConversationIds);
    }

    /**
     * Adds a conversation ID to the list of conversation IDs.
     * @param conversationId: The id to add
     */
    public void addConversation(UUID conversationId) {
        conversationIds.add(conversationId);
    }

    public void deleteConversation(UUID conversationId) { conversationIds.remove(conversationId); }

    /**
     * Sets the list of conversation IDs
     * @param conversationIds: The new list of conversation IDs
     */
    public void setConversationIds(ArrayList<UUID> conversationIds) {
        this.conversationIds = conversationIds;
    }

    /**
     * returns true if the user can message the receiver
     * @param receiver: String of the username of the receiver.
     * @return boolean indicating if they can or not.
     */
    public boolean canMessage(String receiver) {
        return contacts.contains(receiver);
    }

    /**
     *
     * @return list of contacts that this user has stored
     */
    public List<String> getContacts() {
        return new ArrayList<>(this.contacts);
    }

    /**
     * Adds a contact ID to the list of contact IDs
     * @param contactId: The contact ID to add
     */
    public void addContact(String contactId) {
        // Should this method maybe add to or remove from the list rather then set the whole thing at once? -AW 11/5
        // I changed this method to update the list with single IDs - KT 11/8
        if (!canMessage(contactId)) contacts.add(contactId);
    }

    /**
     * Removes a contact ID form the list of contact IDs
     * @param contactId: The id to remove
     */
    public void removeContact(String contactId) {
        if (canMessage(contactId)) contacts.remove(contactId);
    }

    /**
     *
     * @return hasCovid
     */
    public boolean getCovidStatus() {return hasCovid;}

    /**
     *
     * @param status sets hasCovid
     */
    public void setCovidStatus(boolean status) {hasCovid = status;}

    /**
     *
     * @param conversationId : the conversation id for the conversation to be archived
     */
    public void archiveConversation(UUID conversationId) {
        if (!(conversationIds.contains(conversationId))) {
            return;
        }
        this.archivedConversationIds.add(conversationId);
        this.conversationIds.remove(conversationId);
    }

    /**
     *
     * @return true iff this is an Organizer
     */
    public boolean isOrganizer() {return false;}

    /**
     *
     * @return true iff this is a Speaker
     */
    public boolean isSpeaker() {return false;}

    /**
     *
     * @return true iff this is an Admin
     */
    public boolean isAdminUser() {return false;}

    /**
     *
     * @return true iff this is a Attendee
     */
    public boolean isAttendee(){
        return false;
    }

    /**
     *
     * @return true iff this is a VIP User
     */
    public boolean isVIPUser(){return false;}

}