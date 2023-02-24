package org.example;

import AnnotatedSentence.AnnotatedWord;

import java.util.ArrayList;
import java.util.List;

public class WordGroup {

    private ArrayList<String> options;
    private OptionType optionType;

    public WordGroup(String groupText){
        options = new ArrayList<String>(List.of(groupText.split("/")));
        if (options.get(0).toUpperCase().equals(options.get(0))){
            optionType = OptionType.TAG;
        } else {
            optionType = OptionType.WORD;
        }
    }

    public boolean isComplexGroup(){
        if (optionType.equals(OptionType.WORD)){
            return false;
        }
        if (options.size() == 1 && !options.get(0).equals("PP")){
            return false;
        }
        if (options.size() > 1 && options.get(0).startsWith("N")){
            return true;
        }
        return false;
    }

    public boolean wordMatch(AnnotatedWord word){
        if (optionType.equals(OptionType.WORD)){
            for (String option : options){
                if (option.equalsIgnoreCase(word.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tagMatch(AnnotatedWord word){
        if (optionType.equals(OptionType.TAG)){
            for (String option : options){
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
