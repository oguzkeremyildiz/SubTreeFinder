package org.example;

import Cookies.Tuple.Pair;
import DataStructure.CounterHashMap;
import ParseTree.*;

import java.io.*;
import java.util.*;

public class QnoConstituencyRulesGenerator {

    private static Pair<Integer, Integer> dfs(HashMap<Integer, ArrayList<Pair<Integer, String>>> rangeMap, ParseNode current, int index, HashMap<String, String> tagMap) {
        if (current.isLeaf()) {
            return new Pair<>(index + 1, index + 1);
        }
        int lastIndex = index;
        for (int i = 0; i < current.numberOfChildren(); i++) {
            ParseNode child = current.getChild(i);
            Pair<Integer, Integer> p = dfs(rangeMap, child, lastIndex, tagMap);
            lastIndex = p.getValue();
        }
        String currentData = current.getData().getName();
        String childData = current.getChild(0).getData().getName().toLowerCase();
        if (childData.equals("why") || childData.equals("whose") || childData.equals("whom") || childData.equals("had") || childData.equals("may") || childData.equals("been") || childData.equals("until") || childData.equals("have") || childData.equals("has") || childData.equals("since") || childData.equals("of") || childData.equals("will") || childData.equals("be") || childData.equals("would") || childData.equals("must") || childData.equals("could") || childData.equals("can") || childData.equals("does") || childData.equals("did") || childData.equals("do") || childData.equals("who") || childData.equals("how") || childData.equals("much") || childData.equals("many") || childData.equals("what") || childData.equals("when") || childData.equals("where") || childData.equals("which") || childData.equals("is") || childData.equals("are") || childData.equals("was") || childData.equals("were")) {
            if (!rangeMap.containsKey(index)) {
                rangeMap.put(index, new ArrayList<>());
            }
            rangeMap.get(index).add(new Pair<>(lastIndex - 1, childData));
        } else if (tagMap.containsKey(currentData)) {
            if (!rangeMap.containsKey(index)) {
                rangeMap.put(index, new ArrayList<>());
            }
            rangeMap.get(index).add(new Pair<>(lastIndex - 1, currentData));
        }
        return new Pair<>(index, lastIndex);
    }

    private static Pair<HashMap<Integer, ArrayList<Pair<Integer, String>>>, Integer> calculateNodeRange(ParseNode root, HashMap<String, String> tagMap) {
        HashMap<Integer, ArrayList<Pair<Integer, String>>> rangeMap = new HashMap<>();
        Pair<Integer, Integer> p = dfs(rangeMap, root, 0, tagMap);
        return new Pair<>(rangeMap, p.getValue() - 1);
    }

    private static String toString(ArrayList<String> list) {
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            result.append(s).append(" ");
        }
        return result.toString();
    }

    private static void backtrack(HashMap<Integer, ArrayList<Pair<Integer, String>>> pairMap, HashMap<String, String> tagMap, CounterHashMap<String> possibilities, ArrayList<String> possibility, int lastIndex, int treeSize) {
        if (lastIndex - 1 == treeSize) {
            possibilities.put(toString(possibility));
        } else {
            if (pairMap.containsKey(lastIndex)) {
                ArrayList<Pair<Integer, String>> candidates = pairMap.get(lastIndex);
                for (Pair<Integer, String> candidate : candidates) {
                    possibility.add(tagMap.get(candidate.getValue()));
                    backtrack(pairMap, tagMap, possibilities, possibility, candidate.getKey() + 1, treeSize);
                    possibility.remove(possibility.size() - 1);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TreeBank bank = new TreeBank(new File("Trees"));
        Scanner source = new Scanner(new File("automatic-qno-search-list.txt"));
        CounterHashMap<String> possibilities = new CounterHashMap<>();
        HashMap<String, String> tagMap = new HashMap<>();
        while (source.hasNextLine()) {
            String line = source.nextLine();
            String[] tokens = line.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                String[] parts = tokens[i].split("/");
                if (!tagMap.containsKey(parts[0])) {
                    for (String part : parts) {
                        tagMap.put(part, tokens[i]);
                    }
                }
            }
        }
        source.close();
        for (int i = 0; i < bank.size(); i++) {
            ParseTree parseTree = bank.get(i);
            ParseNode root = parseTree.getRoot();
            Pair<HashMap<Integer, ArrayList<Pair<Integer, String>>>, Integer> pair = calculateNodeRange(root, tagMap);
            backtrack(pair.getKey(), tagMap, possibilities, new ArrayList<>(), 0, pair.getValue());
        }
        List<Map.Entry<String, Integer>> topNList = possibilities.topN(1000);
        for (Map.Entry<String, Integer> entry : topNList) {
            System.out.println(entry.getKey() + "-> " + entry.getValue());
        }
    }
}
