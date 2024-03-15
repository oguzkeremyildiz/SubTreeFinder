package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SentenceList {

    private final HashMap<Integer, SentenceOfPhrases> sentences;

    public SentenceList(String fileName){
        try {
            Scanner input = new Scanner(new File(fileName));
            sentences = new HashMap<>();
            while (input.hasNextLine()){
                String s = input.nextLine();
                sentences.put(Integer.parseInt(s.substring(0, s.indexOf(" "))), new SentenceOfPhrases(s.substring(s.indexOf(" ") + 1)));
            }
            input.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SentenceOfPhrases getSentence(int key){
        return sentences.get(key);
    }
}
