package org.example;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedPhrase;
import AnnotatedSentence.AnnotatedWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AutomaticDependencyAssignment {

    private static void printPhraseList(AnnotatedSentence sentence, ArrayList<AnnotatedPhrase> phraseList, String qno){
        System.out.print(sentence.getFileName() + " " + qno + " ");
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

    private static AnnotatedPhrase getGroupWithIndex(ArrayList<AnnotatedPhrase> phraseList, String index){
        for (AnnotatedPhrase annotatedPhrase : phraseList){
            if (annotatedPhrase.getTag().equals(index)){
                return annotatedPhrase;
            }
        }
        return null;
    }

    private static boolean connectDependencies(ArrayList<AnnotatedPhrase> phraseList, SentenceOfPhrases dependencies){
        boolean changed = false;
        for (AnnotatedPhrase annotatedPhrase : phraseList){
            if (annotatedPhrase.wordCount() == 1){
                AnnotatedWord annotatedWord = (AnnotatedWord) annotatedPhrase.getWord(0);
                Phrase connectedGroup = dependencies.getPhrase(Integer.parseInt(annotatedPhrase.getTag()) - 1);
                if (connectedGroup != null){
                    String toGroupIndex = connectedGroup.getPart(0);
                    String dependencyType = connectedGroup.getPart(1);
                    AnnotatedPhrase toGroup = getGroupWithIndex(phraseList, toGroupIndex);
                    if (toGroup != null && toGroup.wordCount() == 1){
                        int toWordIndex;
                        if (dependencyType.equals("root")){
                            toWordIndex = 0;
                        } else {
                            toWordIndex = toGroup.getWordIndex() + 1;
                        }
                        annotatedWord.setUniversalDependency(toWordIndex, dependencyType);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    public static void main(String[] args) throws FileNotFoundException {
        SentenceList automaticDependencies = new SentenceList("automatic-dependencies.txt");
        SentenceList sentenceList = new SentenceList("questionbank-penn-search-list.txt");
        Scanner input = new Scanner(new File("questionbank-qno.txt"));
        AnnotatedCorpus corpus = new AnnotatedCorpus(new File("../../QuestionBank/English-Phrase/"), ".train");
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            String qno = input.nextLine();
            if (!qno.isEmpty()){
                SentenceOfPhrases sentenceOfPhrases = sentenceList.getSentence(Integer.parseInt(qno));
                ArrayList<AnnotatedPhrase> phraseList = sentenceOfPhrases.findGroups(sentence);
                if (phraseList.size() != sentenceOfPhrases.groupSize() + 1){
                    System.out.print(" *PARTIAL MATCH* " + qno + " ");
                }
                printPhraseList(sentence, phraseList, qno);
                SentenceOfPhrases dependencies = automaticDependencies.getSentence(Integer.parseInt(qno));
                if (connectDependencies(phraseList, dependencies)){
                    sentence.save();
                }
            }
        }
        input.close();
    }
}
