package org.example;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedPhrase;
import Dictionary.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AutomaticDependencyAssignment {

    private static void printPhraseList(AnnotatedSentence sentence, ArrayList<AnnotatedPhrase> phraseList){
        System.out.print(sentence.getFileName() + " ");
        System.out.println(sentence.toStems());
        for (AnnotatedPhrase annotatedPhrase : phraseList){
            System.out.print("[");
            for (int j = 0; j < annotatedPhrase.wordCount(); j++){
                System.out.print(annotatedPhrase.getWord(j).getName());
                if (j != annotatedPhrase.wordCount() - 1){
                    System.out.print(" ");
                }
            }
            System.out.print("]");
        }
        System.out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        SearchList searchList = new SearchList("questionbank-penn-search-list.txt");
        Scanner input = new Scanner(new File("questionbank-qno.txt"));
        AnnotatedCorpus corpus = new AnnotatedCorpus(new File("../../QuestionBank/English-Phrase/"), ".train");
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            String qno = input.nextLine();
            if (!qno.isEmpty()){
                SearchSentence searchSentence = searchList.getSentence(Integer.parseInt(qno));
                ArrayList<AnnotatedPhrase> phraseList = searchSentence.findGroups(sentence);
                if (phraseList.size() != searchSentence.groupSize() + 1){
                    System.out.print(" *PARTIAL MATCH* " + qno + " ");
                }
                printPhraseList(sentence, phraseList);
            }
        }
        input.close();
    }
}
