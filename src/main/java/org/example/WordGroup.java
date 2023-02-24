package org.example;

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
}
