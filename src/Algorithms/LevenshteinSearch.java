package Algorithms;

import java.util.ArrayList;
import java.util.List;

public abstract class LevenshteinSearch {
    public final int distance;

    public LevenshteinSearch(int distance) {
        this.distance = distance;
    }

    /**
     *
     * Finds the Levenshtein distance between two strings
     * @param s first string
     * @param t second string
     * s and t are assumed to be words (strings with no spaces)
     */
    int levenshtein(String s, String t) {
        return distance(s.toCharArray(), t.toCharArray());
    }

    /**
     * Creates a matrix using the Wagner-Fischer algorithm to find the edit distance between two character arrays
     */
    private int distance(char[] s, char[] t) {
        int cost;

        int[][] d = new int[s.length + 1][t.length + 1];
        for (int i = 0; i < s.length + 1; i++) {
            d[i][0] = i;
        }
        for (int j = 1; j < t.length + 1; j++) {
            d[0][j] = j;
        }

        for (int j = 1; j < t.length + 1; j++) {
            for (int i = 1; i < s.length + 1; i++) {
                if (s[i-1] != t[j-1]) {
                    cost = 1;
                }
                else {cost = 0;}
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        return d[s.length][t.length];
    }

    /**
     * Calculates minimum of three integers
     */
    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
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


}
