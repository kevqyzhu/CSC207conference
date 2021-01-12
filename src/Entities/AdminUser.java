package Entities;

public class AdminUser extends User{

    public AdminUser(String userId, String password) {
        super(userId, password);
    }

    /**
     * states if this object is a Admin user
     * @return Boolean value that returns true since this is an Admin user object
     */
    @Override
    public boolean isAdminUser(){
        return true;
    }


}
