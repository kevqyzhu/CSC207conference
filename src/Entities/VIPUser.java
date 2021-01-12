package Entities;


public class VIPUser extends Attendee {
    public VIPUser(String userId, String password) {
        super(userId, password);
    }

    /**
     * states if this object is a VIP user
     * @return Boolean value that returns true since this is an VIP user object
     */
    @Override
    public boolean isVIPUser() {
        return true;
    }

}
