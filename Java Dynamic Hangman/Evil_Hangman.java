import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
/**
 * Write a description of class Main here.
 * 
 * @author Carlo Hormoz
 * @version Final Version
 */
public class Evil_Hangman
{  
    public static void main(String [] args) throws FileNotFoundException {
        String playAgain = "yes";
        do {
            System.out.println('\f');
            Scanner keyboard = new Scanner(System.in);
            
            int wordLength = 0;                                         // The user-input length of the word to guess
            int guesses = 0;                                            // Value to store the number of user guesses remaining.
            char currGuess;                                             // The current letter guessed by the user
            String currPattern;                                         // The current (String) pattern displayed to the user
            ArrayList<Character> guessed = new ArrayList<Character>();  // An array to store the user guesses throughout the game.
            
            System.out.println("Enter a word length to start the game");

            wordLength = keyboard.nextInt();
            while (wordLength < 2 || wordLength > 20) {  // Check for valid input
                System.out.println("Please enter a valid word length");
                wordLength = keyboard.nextInt();
            }
            ArrayList<String> iDictionary = createInitialDictionary(wordLength);
            System.out.println("How many guesses do you want to start with? (Between 1-25)");
            while (guesses <=0 || guesses > 25) {
                guesses = keyboard.nextInt();
            }

            System.out.println("Guess a letter to start the game");     // Start the first iteration of the main part of the game
            currGuess = keyboard.next().toLowerCase().charAt(0);
            guessed.add(currGuess);
            System.out.println('\f');

            HashMap<String,ArrayList<String>> wordFamily = new HashMap();
            wordFamily = pattern(iDictionary,currGuess);
            currPattern = wordFamily.keySet().iterator().next();
            if (currPattern.indexOf(currGuess)== -1) {      // If the "secret word" does not contain the users guess.
                guesses--;
                System.out.println("Sorry,the secret word does not contain your guess: '" + currGuess + "'");
                System.out.println();
            }
            // System.out.println(wordFamily);
            System.out.println("Your word " + currPattern + "      Remaining Guesses " + guesses);
            System.out.println();

            // System.out.println(iDictionary);
            do {
                System.out.println("Input another letter to guess");
                currGuess = keyboard.next().toLowerCase().charAt(0);
                System.out.println('\f');
                while(currGuess < 'a' || currGuess > 'z' || guessed.contains(currGuess)) {
                    System.out.println("Please enter a valid letter, one that you haven't chosen before.");
                    System.out.println("Your previous guesses are " + guessed);
                    currGuess = keyboard.next().toLowerCase().charAt(0);
                }
                guessed.add(currGuess);
                System.out.println();
                wordFamily = updatePattern(currPattern,wordFamily.get(currPattern),currGuess);
                currPattern = wordFamily.keySet().iterator().next();
                if (currPattern.indexOf(currGuess)== -1) {      // If the "secret word" does not contain the users guess.
                    guesses--;
                    System.out.println("Sorry,the secret word does not contain your guess: '" + currGuess + "'");
                    System.out.println();
                }
                else {      // If user guess was correct
                    System.out.println("Correct!");
                    System.out.println();
                    System.out.println();
                }
                System.out.println("Your word " + currPattern + "      Remaining Guesses " + guesses);
                System.out.println();
                Collections.sort(guessed);
                System.out.println("Your previous guesses are...");
                System.out.println(guessed);
                System.out.println();
            } while (guesses >0 && currPattern.indexOf('*') != -1);     // Execute while user has guesses and the word hasn't been guessed.

            if (guesses <=0) {      // Losing statement.
                System.out.println("You lose!");
                System.out.println("The secret word was " + wordFamily.values().iterator().next().get(1));
                System.out.println();
                System.out.println("What a great word");
                System.out.println();
                System.out.println();
            }
            else if (currPattern.indexOf('*') == -1) {      // Winning statements.
                System.out.println("Congratulations, you guessed the secret word");
                System.out.println();
                System.out.println("The secret word was " + wordFamily.values().iterator().next().get(0) + "!");
                System.out.println();
                System.out.println("What a great word");
                System.out.println();
                System.out.println();
            }
            System.out.println("Would you like to play again? (Please enter yes to start over)");
            playAgain = keyboard.next().toLowerCase();
        } while (playAgain.equals("yes"));
    }

    private static ArrayList<String> createInitialDictionary(int length)throws FileNotFoundException { 
        ArrayList<String> iDictionary = new ArrayList<String>();
        Scanner s = new Scanner(new File("dictionary.txt"));
        String temp;
        while (s.hasNext()) {
            temp = s.next();
            if (temp.length() == length) {
                iDictionary.add(temp);
            }
        }
        s.close();
        return iDictionary;
    }
    // Change to a string return
    private static HashMap<String,ArrayList<String>> pattern(ArrayList<String> iDictionary,char guess) {
        HashMap<String,ArrayList<String>> family = new HashMap();
        for (String s: iDictionary) {  // For each string in the dictionary containing all words of the user-input length
            String temp = "";          // Initialize a temporary string to help with creating each pattern.
            for (int i = 0;i< s.length(); i++) {        // Replace each instance of 'guess' with corresponding index in temp String.
                if (s.charAt(i) == guess) {
                    if (i == 0) {
                        temp = Character.toString(guess);
                    }
                    else {      
                        temp+= guess;   
                    }
                }        
                else {          // If there is not a match at the index, place an asterisk.
                    if (i == 0) {
                        temp = Character.toString('*');
                    }
                    else {
                        temp += '*';
                    }
                }
            }
            if (family.containsKey(temp)) {     // If the key exists, remove the associated wordlist and add the current parsed string to it.
                ArrayList tempList = new ArrayList<>(family.get(temp));
                tempList.add(s);
                family.put(temp,tempList);
            }
            else {      // Create an entry for this pattern if not found in the current family.
                ArrayList<String> tempList = new ArrayList<String>();
                tempList.add(s);
                family.put(temp,tempList);
            }
        }
        // System.out.println(family);  
        Map.Entry<String,ArrayList<String>> maxEntry = null;     // This block of code finds the largest size arraylist and saves it and its corresponding pattern to maxEntry.
        for(Map.Entry<String,ArrayList<String>> entry : family.entrySet()) {
            if (maxEntry == null || entry.getValue().size() > maxEntry.getValue().size()) {
                maxEntry = entry;
            }
        }
        family.clear();
        family.put(maxEntry.getKey(),maxEntry.getValue());       // Clear the current wordFamily and insert the maxEntry key-pattern and associated ArrayList
        return family;
    }

    private static HashMap<String,ArrayList<String>> updatePattern(String currPattern, ArrayList<String> currFamily,char guess) {
        HashMap<String,ArrayList<String>> family = new HashMap();
        for (String s: currFamily) {    // For each string in the current wordFamily
            String temp = "";           // Initialize the temporary string that will be used to create each individual pattern
            for (int i = 0;i< s.length(); i++) {      // Iterate through each word, placing the users guess where there is a match at the respective index of the temp string.
                if (s.charAt(i) == guess) {
                    if (i == 0) {
                        temp = Character.toString(guess);
                    }
                    else {
                        temp += guess;
                    }
                }        
                else {      // If there is not a match at the index, place an asterisk.
                    if (i == 0) {
                        temp = Character.toString(currPattern.charAt(i));
                    }
                    else {
                        temp += currPattern.charAt(i);
                    }
                }
            }
            if (family.containsKey(temp)) {     // If the key exists, remove the associated wordlist and add the current parsed string to it.
                ArrayList tempList = new ArrayList<>(family.get(temp));
                tempList.add(s);
                family.put(temp,tempList);      // Reinsert the updated wordlist
            }
            else {  // Create an entry for this pattern if not found in the current family.
                ArrayList<String> tempList = new ArrayList<String>();
                tempList.add(s);
                family.put(temp,tempList);
            }
        }
        // System.out.println(family);
        Map.Entry<String,ArrayList<String>> maxEntry = null;        // This block of code finds the largest size arraylist and saves it and its corresponding pattern to maxEntry.
        for(Map.Entry<String,ArrayList<String>> entry : family.entrySet()) {
            if (maxEntry == null || entry.getValue().size() > maxEntry.getValue().size()) {
                maxEntry = entry;
            }
        }
        family.clear();
        family.put(maxEntry.getKey(),maxEntry.getValue());      // Clear the current wordFamily and insert the maxEntry key-pattern and associated ArrayList
        return family;
    }
}

