package Algorithms;

import java.util.*;


public class SearchString extends LevenshteinSearch implements Search{

    public SearchString(int distance) {
        super(distance);
    }

    /**
     * Turns a list of strings into a list of vocab (non-repeating words that make up the strings)
     */
    public List<String> getVocab(List<String> words) {
        List<String> vocabDuplicates = new ArrayList<>();
        for (String string : words) {
            vocabDuplicates.addAll(Arrays.asList(string.split(" ")));
        }
        return new ArrayList<>(new HashSet<>(vocabDuplicates));
    }

    /**
     * @param input a string input
     * @param vocab a list of non-repeating words
     * Generates a list of words from an input string.
     * Finds all words in a vocab list that are similar to the list of words.
     * Similarity is judged by the levenshtein distance threshold
     */
    public List<String> getSimilarVocab(String input, List<String> vocab) {
        String[] inputSplit = input.split(" ");

        List<String> matches = new ArrayList<>();
        for (String value : vocab) {
            for (String s : inputSplit) {
                if (levenshtein(value, s) <= distance || value.contains(s)) {
                    matches.add(value);
                    break;
                }
            }
        }
        return matches;
    }

    /**
     * @param given a list of strings being searched
     * @param matches a list of words being matched
     * Finds all strings in given that contain a word in matches
     */
    public List<String> getStringsContainingWords(List<String> given, List<String> matches) {
        List<String> similarStrings = new ArrayList<>();
        for (String value : given) {
            for (String s : matches) {
                if (value.contains(s)) {
                    similarStrings.add(value);
                    break;
                }
            }
        }
        return  similarStrings;
    }

    /**
     * @param input an input string
     * @param options a list of strings being searched
     * Finds all strings in a list of options that are similar to the input string
     */
    public List<String> search(String input, List<String> options) {
        List<String> vocab = getVocab(options);
        List<String> matches = getSimilarVocab(input, vocab);
        return getStringsContainingWords(options, matches);

    }

}

