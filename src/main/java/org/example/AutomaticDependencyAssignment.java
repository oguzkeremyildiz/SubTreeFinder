package org.example;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AutomaticDependencyAssignment {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(new File("questionbank-qno.txt"));
        AnnotatedCorpus corpus = new AnnotatedCorpus(new File("../../QuestionBank/English-Phrase/"), ".train");
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            String qno = input.nextLine();
            for (int j = 0; j < sentence.wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
            }
        }
        input.close();
    }
}
