package hangman;

import java.io.File;
import java.util.Scanner;
import java.util.Set;

public class Main {

    /**
     * Plays a game of Hangman where the computer cheats
     *
     * @param args [0-x]dictionary with whitespace, [x + 1]wordLength, [x + 2]guesses
     */
    public static void main (String[] args) {
        int wordLength = 0;
        int guesses = 0;
        String filePath = "";
        File dictionary = null;

        try {
            for (int i = 0; i < args.length - 2; i++){
                filePath += args[i] + " ";
            }
            filePath = filePath.trim();
            dictionary = new File(filePath);
            wordLength = Integer.parseInt(args[args.length - 2]);
            guesses = Integer.parseInt(args[args.length - 1]);
            if (wordLength < 2 || guesses < 1) {
                uException();
            }
        }
        catch (NumberFormatException | IndexOutOfBoundsException  e) {
            uException();
        }

        gameLoop(dictionary, wordLength, guesses);

    }

    private static void uException() {
        System.out.println("Usage: java hangman.Main dictionary wordLength guesses");
        System.exit(0);
    }

    private static void gameLoop(File dictionary, int wordLength, int guesses) {

        EvilHangman hang = new EvilHangman();
        hang.startGame(dictionary, wordLength);
        Set possibleWords = null;
        char guess;
        System.out.println("\nWelcome to Hangman!");

        while (guesses > 0) {
            System.out.println("You have " + guesses + " guess(es) left");
            String guessed = hang.getGuessed().toString();
            guessed = guessed.replaceAll("[\\[\\]]","");
            System.out.println("Used Letters: " + guessed);
            System.out.println("Word: " + hang.getCurKey());
            System.out.print("\nGuess a letter: ");

            Scanner sc = new Scanner(System.in);
            guess = sc.next().charAt(0);
            guess = Character.toLowerCase(guess);

            try {
                possibleWords = hang.makeGuess(guess);
            } catch (IEvilHangmanGame.GuessAlreadyMadeException e) {
                System.out.println(e.getMessage());
                continue;
            }

            int count = charCount(guess, hang.getCurKey());

            if (count > 0) {
                if (count > 1) {
                    System.out.println("Yes, there are " + count + " " + guess + "'s");
                }
                else {
                    System.out.println("Yes, there is " + count + " " + guess);
                }
            }
            else {
                System.out.println("Sorry, there are no " + guess + "\'s");
                guesses--;
            }

            if (hang.getCurKey().indexOf('_') < 0) {
                System.out.println("\nYou win!");
                System.out.println("Word: " + hang.getCurKey());
                System.exit(0);
            }
        }

        System.out.println("\nYou lose!");
        if (possibleWords != null) {
            System.out.println("The word was: " + (possibleWords.iterator().next()) + "\n");
            //System.out.println("\nRemaining words " + possibleWords);
        }
    }

    static int charCount(char guess, String word) {
        int count = 0;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                count++;
            }
        }

        return count;
    }


}

