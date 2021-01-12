package ConferenceControllers;

import Algorithms.Search;
import Algorithms.SearchString;
import Algorithms.SearchWord;
import ConferenceControllers.MainMenu.MainMenu;
import UI.InterfacePresenter;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ConferenceController {
    public BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public InterfacePresenter presenter = new InterfacePresenter();
    MainMenu mainMenu;

    public ConferenceController(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public ConferenceController() {
    }

    /**
     * Handle exceptions is a method which contains rules for exit and help
     * if the user types "help" in the terminal then then the help menu would be displayed by the presenter.printHelp()
     * and in it if a error occurs a´the Exception is thrown
     * Else if the user types "exit" then the program will display the exit menu in the terminal and the navigate to
     * the main menu
     * @param input: Takes in a keyboard input from the terminal
     * @throws Exception is thrown if help is not processed
     */
    public void handleExceptions(String input) throws Exception {
        if (input.toLowerCase().contains("help")) {
            presenter.printHelp();
            throw new Exception();
        } else if (input.toLowerCase().contains("exit")) {
            presenter.printExit();
            mainMenu.handleMainMenu();
        } else if (input.toLowerCase().contains("quit")) {
            presenter.printTerminate();
            if (mainMenu != null) {
                mainMenu.exit();
            }
        }
    }

    /**
     * The reader reads the line and returns it
     * @return parser for the input
     * @throws Exception if the reader is unable to read the line
     */
    public String parser() throws Exception {
        String line;
        try {
            line = reader.readLine();
        } catch (Exception e) {
            presenter.printError(e.getMessage());
            throw new Exception();
        }
        handleExceptions(line);
        return line;
    }

    /**
     * Returns a list of strings that matches the keyword input
     * @param searchList the list of strings being searched
     */
    public List<String> handleSearch(List<String> searchList) {
        try {
            presenter.printRequest("Please enter some keywords.");
            String input = parser();
            Search search = new SearchString(2);
            ArrayList<String> similar = (ArrayList<String>) search.search(input, searchList);
            if (!similar.isEmpty()) {
                presenter.print("Here are the results that matched your keywords.");
                presenter.printOptions(similar);
            } else {
                presenter.printError("Sorry, no results match your keywords.");
                handleSearch(searchList);
            }
            return similar;

        } catch (Exception e) {
            return handleSearch(searchList);
        }
    }

    /**
     * Returns a list of strings that matches the keyword input
     * @param searchList the list of strings being searched
     */
    public String handleSearchUser(List<String> searchList) {
        try {
            presenter.print("Please enter the ID of the User.");
            presenter.printInput();
            String input = parser();
            Search search = new SearchWord(2);
            ArrayList<String> similar = (ArrayList<String>) search.search(input, searchList);
            if (similar.size() == 1) {
                if(similar.get(0).equals(input)) {
                    return similar.get(0);
                }
                presenter.print("Is this their ID?");
                presenter.printList(similar);
                presenter.printYesNoRequest();
                String decision = checkCommand(parser(), Arrays.asList("yes", "no"));
                if (decision.equals("no")) {
                    presenter.print("Please try again");
                    handleSearchUser(searchList);
                } else { return similar.get(0); }
            } else if (similar.isEmpty()) {
                presenter.printError("Sorry, no users found");
                handleSearchUser(searchList);
            } else {
                presenter.printOptions(similar);
                presenter.printIndexRequest();
                int index = checkInt(parser(), similar.size());
                return similar.get(index);
            }

        } catch (Exception e) {
            return handleSearchUser(searchList);
        }
        return null;
    }

    /**
     * Returns the index for the options for a menu which will be displayed on the terminal
     * @param str the string parameter
     * @param limit of the option or indexes
     * @return keyboard input from the user
     * @throws Exception index out of bound error is thrown if the limit is exceed
     */
    public int checkInt(String str, int limit) throws Exception {
        try {
            int input = Integer.parseInt(str);
            if (input >= 0 && input < limit) return input;
            else throw new IndexOutOfBoundsException();
        } catch(Exception e) {
            presenter.printError(e.getMessage());
            throw new Exception();
        }
    }

    /**
     *
     * @param str input command
     * @param commands list of command
     * @return command from the list of commands
     * @throws Exception error ocuurred and error message is shown
     */
    public String checkCommand(String str, List<String> commands) throws Exception {
        try {
            String input = str.toLowerCase();
            for(String command : commands) {
                if (input.contains(command)) {
                    return command;
                }
            }
        } catch (Exception e) {
            presenter.printError(e.getMessage());
            throw e;
        }
        presenter.printError("no command specified");
        throw new Exception();
    }
}
