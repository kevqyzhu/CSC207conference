package Algorithms;

import java.util.List;

public class SearchWord extends LevenshteinSearch implements Search{

    /**
     * inherits from it's super class for parameter distance
     * @param distance distance integer input
     */
    public SearchWord(int distance) {
        super(distance);
    }

    /**
     * @param input an input string
     * @param options a list of unique words being searched
     * Finds all strings in a list of options that are similar to the input string
     */
    public List<String> search(String input, List<String> options) {
        return getSimilarVocab(input, options);
    }




}
