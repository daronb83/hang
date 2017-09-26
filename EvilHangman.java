package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

class EvilHangman implements IEvilHangmanGame {

    private Set<String> wordSet;
    private TreeSet<Character> guessed;
    private String curKey;

    /**
     * Cheats at hangman
     */
    EvilHangman() {
        guessed = new TreeSet<>();
        wordSet = new HashSet<>();
        curKey = "";
    }


    /**
     * Starts a new game of evil hangman using wordSet from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of wordSet to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength) {
        curKey = "";

        for (int i = 0; i < wordLength; i++) {
            curKey += '_';
        }

        try (Scanner inFile = new Scanner(dictionary)) {

            while (inFile.hasNext()) {
                String word = inFile.next();

                if (word.length() == wordLength) {
                    wordSet.add(word);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these wordSet had been the secret word for the whole game.
     *
     * @throws GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        if (guessed.contains(guess)) {
            throw new GuessAlreadyMadeException("You already used that letter");
        }
        else if (!Character.isAlphabetic(guess)) {
            throw new GuessAlreadyMadeException("Invalid input");
        }
        else {
            guessed.add(guess);
            HashMap<String, HashSet<String>> map = new HashMap<>();

            // Separate all words into sets based on where the letter appears
            for (String word : wordSet) {
                String key = getKey(word, guess);

                if (map.containsKey(key)) {
                    map.get(key).add(word);
                }
                else {
                    HashSet<String> set = new HashSet<>();
                    set.add(word);
                    map.put(key, set);
                }
            }

            // Find the largest set
            wordSet = getBestSet(map, guess);
            return wordSet;
        }
    }

    /**
     * Returns the best set in the map based on set length and the character position(s) of guess
     *
     * @param map generated by splitting wordSet into pieces based on guess positions
     * @param guess the character that was guessed
     * @return the best set in the map based on set length and the character position(s) of guess
     */
    private HashSet<String> getBestSet(HashMap<String, HashSet<String>> map, char guess) {
        int maxSize = 0;
        String maxKey = "";

        for (HashMap.Entry<String, HashSet<String>> entry : map.entrySet()) {
            HashSet<String> set = entry.getValue();
            String key = entry.getKey();

            if (set.size() > maxSize){
                maxSize = set.size();
                maxKey = key;
            }
            else if (set.size() == maxSize) {
                if (getWeight(key, guess) < getWeight(maxKey, guess)) {
                    maxKey = key;
                }
            }
        }

        curKey = maxKey;
        return map.get(maxKey);
    }

    /**
     * Returns a weight value that can be used to compare set keys.
     * Lower values are more desirable.
     *
     * @param key the key to assign a value to
     * @param guess the character being guessed
     * @return a weight value that can be used to compare set keys
     */
    private int getWeight(String key, char guess) {
        int weight = 0;

        for (int i = 1; i < key.length(); i++) {

            if (key.charAt(key.length() - i - 1) == guess) {
                weight += key.length() + i;
            }
        }

        return weight;
    }

    /**
     * Returns a string that shows where currently guessed letters are found in the word.
     * Doubles as both a set key and for displaying to the user
     *
     * @param word the word needing a key representation
     * @return a string that shows where currently guessed letters are found in the word.
     */
    private String getKey(String word, char guess) {
        String key = "";

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                key += guess;
            }
            else {
                key += curKey.charAt(i);
            }
        }

        return key;
    }

    /**
     * @return the current key
     */
    String getCurKey() {
        return curKey;
    }

    /**
     * @return the set of characters that have been guessed
     */
    TreeSet<Character> getGuessed() {
        return guessed;
    }
}
