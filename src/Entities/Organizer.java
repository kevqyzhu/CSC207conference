package Entities;

import java.io.Serializable;

public class Organizer extends User implements Serializable {
    public Organizer(String userId, String password) {
        super(userId, password);
    }

    /**
     * states if this object is an organizer
     * @return Boolean value that returns true since this is an organizer object
     */
    @Override
    public boolean isOrganizer() {return true;}
}
