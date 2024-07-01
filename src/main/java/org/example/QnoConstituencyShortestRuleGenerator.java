package org.example;

import Cookies.Tuple.Pair;
import DataStructure.CounterHashMap;
import ParseTree.*;
import ParseTree.TreeBank;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class QnoConstituencyShortestRuleGenerator extends QnoConstituencyRulesGenerator {

    private static boolean isQuestion(String str) {
        return str.equals("why") || str.equals("who") || str.equals("what") || str.equals("how") || str.equals("when") || str.equals("which") || str.equals("where");
    }

    private static String backtrack(HashMap<Integer, ArrayList<Pair<Integer, String>>> pairMap, HashMap<String, String> tagMap, ArrayList<String> possibility, int lastIndex, int treeSize, String best) {
        if (lastIndex - 1 == treeSize) {
            String cur = toString(possibility);
            if (best.length() > cur.length() && (isQuestion(possibility.get(0).toLowerCase()) || (possibility.size() > 1 && isQuestion(possibility.get(1).toLowerCase())))) {
                return cur;
            }
        } else {
            if (pairMap.containsKey(lastIndex)) {
                ArrayList<Pair<Integer, String>> candidates = pairMap.get(lastIndex);
                for (Pair<Integer, String> candidate : candidates) {
                    possibility.add(tagMap.get(candidate.getValue()));
                    best = backtrack(pairMap, tagMap, possibility, candidate.getKey() + 1, treeSize, best);
                    possibility.remove(possibility.size() - 1);
                }
            }
        }
        return best;
    }


    public static void main(String[] args) throws IOException {
        TreeBank bank = new TreeBank(new File("Trees"));
        CounterHashMap<String> possibilities = new CounterHashMap<>();
        HashMap<String, String> tagMap = generateTagMap();
        for (int i = 0; i < bank.size(); i++) {
            ParseTree parseTree = bank.get(i);
            ParseNode root = parseTree.getRoot();
            Pair<HashMap<Integer, ArrayList<Pair<Integer, String>>>, Integer> pair = calculateNodeRange(root, tagMap);
            possibilities.put(backtrack(pair.getKey(), tagMap, new ArrayList<>(), 0, pair.getValue(), "NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY"));
        }
        List<Map.Entry<String, Integer>> topNList = possibilities.topN(1001);
        for (Map.Entry<String, Integer> entry : topNList) {
            if (!entry.getKey().equals("NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY_NO_POSSIBILITY")) {
                System.out.println(entry.getKey() + entry.getValue());
            }
        }
    }
}
