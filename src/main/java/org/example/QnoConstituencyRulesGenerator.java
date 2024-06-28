package org.example;

import Cookies.Tuple.Pair;
import DataStructure.CounterHashMap;
import ParseTree.*;

import java.io.*;
import java.util.*;

public class QnoConstituencyRulesGenerator {

    private static ArrayList<ParseNode> dfs(HashMap<ParseNode, ArrayList<ParseNode>> rangeMap, ParseNode current) {
        ArrayList<ParseNode> result = new ArrayList<>();
        if (current.isLeaf()) {
            result.add(current);
            return result;
        }
        for (int i = 0; i < current.numberOfChildren(); i++) {
            ParseNode child = current.getChild(i);
            result.addAll(dfs(rangeMap, child));
        }
        rangeMap.put(current, (ArrayList<ParseNode>) result.clone());
        return result;
    }

    private static HashMap<ParseNode, ArrayList<ParseNode>> calculateNodeRange(ParseNode root) {
        HashMap<ParseNode, ArrayList<ParseNode>> rangeMap = new HashMap<>();
        dfs(rangeMap, root);
        return rangeMap;
    }

    private static HashMap<ParseNode, Integer> calculateNoMap(ArrayList<ParseNode> leaves) {
        HashMap<ParseNode, Integer> map = new HashMap<>();
        for (int i = 0; i < leaves.size(); i++) {
            map.put(leaves.get(i), i);
        }
        return map;
    }

    private static void calculatePairMap(HashMap<Integer, ArrayList<Pair<Integer, String>>> pairMap, HashSet<String> tags, HashMap<ParseNode, ArrayList<ParseNode>> rangeMap, HashMap<ParseNode, Integer> noMap, ParseNode current) {
        if (!current.isLeaf()) {
            String currentData = current.getData().getName();
            String childData = current.getChild(0).getData().getName();
            ArrayList<ParseNode> range = rangeMap.get(current);
            // do, did, does will be added.
            if (childData.equals("how") || childData.equals("much") || childData.equals("many") || childData.equals("what") || childData.equals("when") || childData.equals("where") || childData.equals("which") || childData.equals("is") || childData.equals("are") || childData.equals("was") || childData.equals("were")) {
                if (!pairMap.containsKey(noMap.get(range.get(0)))) {
                    pairMap.put(noMap.get(range.get(0)), new ArrayList<>());
                }
                pairMap.get(noMap.get(range.get(0))).add(new Pair<>(noMap.get(range.get(range.size() - 1)), childData));
            } else if (tags.contains(currentData)) {
                if (!pairMap.containsKey(noMap.get(range.get(0)))) {
                    pairMap.put(noMap.get(range.get(0)), new ArrayList<>());
                }
                pairMap.get(noMap.get(range.get(0))).add(new Pair<>(noMap.get(range.get(range.size() - 1)), currentData));
            }
            for (int i = 0; i < current.numberOfChildren(); i++) {
                ParseNode child = current.getChild(i);
                calculatePairMap(pairMap, tags, rangeMap, noMap, child);
            }
        }
    }

    private static String removePunctuations(ArrayList<String> list) {
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            if (!(s.equals(".") || s.equals(","))) {
                result.append(s).append(" ");
            }
        }
        return result.toString();
    }

    private static void backtrack(HashMap<Integer, ArrayList<Pair<Integer, String>>> pairMap, HashMap<String, String> tagMap, CounterHashMap<String> possibilities, ArrayList<String> possibility, int lastIndex, int treeSize) {
        if (lastIndex - 1 == treeSize) {
            possibilities.put(removePunctuations(possibility));
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
        HashSet<String> tags = new HashSet<>();
        HashMap<String, String> tagMap = new HashMap<>();
        HashSet<String> tagSet = new HashSet<>();
        while (source.hasNextLine()) {
            String line = source.nextLine();
            String[] tokens = line.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                String[] parts = tokens[i].split("/");
                Collections.addAll(tags, parts);
                if (!tagSet.contains(tokens[i])) {
                    tagSet.add(tokens[i]);
                    for (String part : parts) {
                        tagMap.put(part, tokens[i]);
                    }
                }
            }
        }
        source.close();
        HashMap<ParseNode, ArrayList<ParseNode>> rangeMap;
        HashMap<ParseNode, Integer> noMap;
        HashMap<Integer, ArrayList<Pair<Integer, String>>> pairMap = new HashMap<>();
        for (int i = 0; i < bank.size(); i++) {
            ParseTree parseTree = bank.get(i);
            ParseNode root = parseTree.getRoot();
            rangeMap = calculateNodeRange(root);
            noMap = calculateNoMap(rangeMap.get(root));
            int treeSize = noMap.get(rangeMap.get(root).get(rangeMap.get(root).size() - 1));
            calculatePairMap(pairMap, tags, rangeMap, noMap, root);
            backtrack(pairMap, tagMap, possibilities, new ArrayList<>(), 0, treeSize);
            pairMap.clear();
        }
        List<Map.Entry<String, Integer>> topNList = possibilities.topN(1000);
        for (Map.Entry<String, Integer> entry : topNList) {
            System.out.println(entry.getKey() + "-> " + entry.getValue());
        }
    }
}
