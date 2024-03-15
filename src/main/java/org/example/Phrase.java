package org.example;

import AnnotatedSentence.AnnotatedWord;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

    private final ArrayList<String> parts;
    private final PhraseType phraseType;

    public Phrase(String groupText){
        parts = new ArrayList<>(List.of(groupText.split("/")));
        if (parts.get(0).toUpperCase().equals(parts.get(0))){
            phraseType = PhraseType.TAG;
        } else {
            phraseType = PhraseType.WORD;
        }
    }

    public String getPart(int index){
        return parts.get(index);
    }

    public boolean isComplexPhrase(){
        if (phraseType.equals(PhraseType.WORD)){
            return false;
        }
        if (parts.size() == 1 && !parts.get(0).equals("PP")){
            return false;
        }
        return parts.size() > 1 && parts.get(0).startsWith("N");
    }

    public boolean wordMatch(AnnotatedWord word){
        if (phraseType.equals(PhraseType.WORD)){
            for (String option : parts){
                if (option.equalsIgnoreCase(word.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean ppmatch(AnnotatedWord word){
        return (word.getPosTag().equals("IN") || word.getPosTag().equals("TO")) && parts.get(0).equals("PP");
    }

    public boolean tagMatch(AnnotatedWord word){
        if (phraseType.equals(PhraseType.TAG)){
            for (String option : parts){
                if (option.equalsIgnoreCase(word.getPosTag())){
                    switch (option){
                        case "VBD":
                        case "VBN":
                        case "VBP":
                        case "VBZ":
                        case "VB":
                        case "IN":
                        case "JJ":
                        case "RB":
                        case "VBG":
                        case "TO":
                        case "RP":
                        case "JJR":
                        case "CC":
                            return true;
                    }
                } else {
                    if (word.getPosTag().startsWith("AUX") && option.equals("AUX")){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
