package ConferenceControllers;

import java.util.ArrayList;

public class AdminUserDecision implements  Decision {

    /**
     * creates list of decisions for conversation
     * adds all conversation actions as strings to list
     * @return list of conversation action options
     */
    @Override
    public ArrayList<String> decision() {
        ArrayList<String> decisions = new ArrayList<>();
        decisions.add("respond");
        decisions.add("compose");
        decisions.add("mark as unread");
        decisions.add("archive");
        decisions.add("delete");
        return decisions;
    }
}
