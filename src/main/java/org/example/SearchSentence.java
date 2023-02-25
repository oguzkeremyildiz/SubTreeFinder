package org.example;

import AnnotatedSentence.AnnotatedPhrase;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.AnnotatedSentence;
import Dictionary.Word;

import java.util.ArrayList;

public class SearchSentence {

    private ArrayList<WordGroup> groups;

    public SearchSentence(String text){
        groups = new ArrayList<>();
        String[] items = text.split(" ");
        for (String item : items) {
            groups.add(new WordGroup(item));
        }
    }

    public int groupSize(){
        return groups.size();
    }

    public ArrayList<AnnotatedPhrase> findGroups(AnnotatedSentence sentence){
        ArrayList<AnnotatedPhrase> phraseList = new ArrayList<AnnotatedPhrase>();
        int groupIndex = 0, wordIndex = 0;
        boolean inPpMatch = false;
        AnnotatedPhrase ppPhrase = null;
        while (groupIndex < groups.size() && wordIndex < sentence.wordCount()){
            WordGroup wordGroup = groups.get(groupIndex);
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
            if (Word.isPunctuation(word.getName())){
                inPpMatch = false;
                wordIndex++;
            } else {
                if (wordGroup.wordMatch(word) || wordGroup.tagMatch(word)){
                    AnnotatedPhrase annotatedPhrase = new AnnotatedPhrase(wordIndex, "" + (groupIndex + 1));
                    annotatedPhrase.addWord(word);
                    phraseList.add(annotatedPhrase);
                    groupIndex++;
                    wordIndex++;
                    inPpMatch = false;
                } else {
                    if (!inPpMatch && wordGroup.ppmatch(word)){
                        ppPhrase = new AnnotatedPhrase(wordIndex, "" + (groupIndex + 1));
                        ppPhrase.addWord(word);
                        phraseList.add(ppPhrase);
                        wordIndex++;
                        inPpMatch = true;
                    } else {
                        if (inPpMatch){
                            ppPhrase.addWord(word);
                            wordIndex++;
                        } else {
                            if (wordGroup.isComplexGroup()){
                                groupIndex++;
                            } else {
                                wordIndex++;
                            }
                        }
                    }
                }
            }
        }
        if (Word.isPunctuation(sentence.getWord(sentence.wordCount() - 1).getName())){
            AnnotatedPhrase annotatedPhrase = new AnnotatedPhrase(sentence.wordCount() - 1, "" + (groups.size() + 1));
            annotatedPhrase.addWord(sentence.getWord(sentence.wordCount() - 1));
            phraseList.add(annotatedPhrase);
        }
        if (phraseList.size() != groups.size() + 1){
            AnnotatedPhrase firstPhrase = phraseList.get(0);
            int firstTag = Integer.parseInt(firstPhrase.getTag());
            if (firstTag == 2){
                AnnotatedPhrase newPhrase = new AnnotatedPhrase(1, "1");
                for (int j = 0; j < firstPhrase.getWordIndex(); j++){
                    newPhrase.addWord(sentence.getWord(j));
                }
                phraseList.add(0, newPhrase);
            }
            for (int i = 0; i < phraseList.size() - 1; i++){
                AnnotatedPhrase phrase = phraseList.get(i);
                int tag = Integer.parseInt(phrase.getTag());
                AnnotatedPhrase phraseNext = phraseList.get(i + 1);
                int tagNext = Integer.parseInt(phraseNext.getTag());
                if (tag + 2 == tagNext){
                    AnnotatedPhrase newPhrase = new AnnotatedPhrase(phrase.getWordIndex() + 1, "" + (tag + 1));
                    for (int j = phrase.getWordIndex() + 1; j < phraseNext.getWordIndex(); j++){
                        newPhrase.addWord(sentence.getWord(j));
                    }
                    phraseList.add(i + 1, newPhrase);
                }
            }
        }
        return phraseList;
    }
}
