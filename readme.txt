To run our program, run the main method at the bottom of the InterfaceController class. When it first runs,
the console will prompt you to enter in a username and password. Here are some of the login credentials for users saved
in the program:


USER TYPE:  USERNAME, PASSWORD

Organizer: "chris", "pineapples123"
Speaker: "he", "haha"
Organizer: "stevens5", "Grapes64"
Organizer: "mbrian2", "hello2000"
Attendee: "1roka", "TacoTuesday"
Attendee: "hnguyen", "0BigMac0"
Attendee: "msali70", "92!@f7"
Attendee: "harper666", "0910"
AdminUser: "Jonathan", "sus"
VIPUser: "JonathanImposter", "sus"

Once signed in, the menu will prompt you to input a series of indexes which will navigate you through different parts of
the program. In any of these menus, you can type "exit" to return to the main menu. Typing "quit" at any point will
quit the program and automatically save data into the .ser files, however it will route you back to login right away.
If you really want to completely quit the program, you can type quit once you are at that login screen (where it asks
for username).


To switch users, you will have to use the "quit" command to be prompted to login again.

As for how data is saved in our program, data will ONLY save if the user exits the program using the "quit" command.
Otherwise, any objects created will not be serialized when the program is closed.



LIST OF FEATURES IMPLEMENTED IN PHASE 2:

Mandatory:
-Custom event durations (no longer 1 hr), allow for multiple speakers or 0 speakers
-Admin users (who can delete messages and events with no attendees), VIP Users who can access VIP Events
-Organizers can create different user accounts
-Each event can have a max number of attendees

Optional:
-Allow users to mark conversations as unread (note that when user A sends a message to User B, it is automatically set to
read for user A, since they sent it. Also, if B goes to respond, even if they exit before they send a message, it will
mark the conv. as readO) and archive conversations.
-Produce a neatly formatted user schedule for the conference (outputs as html)
-System supports dietary restriction requests (and any other such requests)

Custom:
-Search functionality rather than just doing index selection.
-Covid Tracker
