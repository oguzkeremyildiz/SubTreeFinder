package org.example;

import Cookies.Set.DisjointSet;
import Cookies.Tuple.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AutomaticQnoDependencySearch {

    private static int findRoot(String[] s) {
        for (int i = 0; i < s.length; i++) {
            if (s[i].startsWith("root")) {
                return i;
            }
        }
        return -1;
    }
    private static boolean isMatch(String[] sentence, DisjointSet<String> set, LinkedList<String> depList, HashMap<Integer, Pair<String, Integer>> map, LinkedList<HashSet<String>> rules) {
        int i = 0;
        for (int j = 0; j < rules.size(); j++) {
            HashSet<String> strings = rules.get(j);
            if (i >= sentence.length - 1) {
                return false;
            }
            String toString = strings.toString();
            if (!toString.equals(toString.toUpperCase())) {
                if (!strings.contains(sentence[i].toLowerCase())) {
                    return false;
                }
                i++;
            } else {
                Pair<String, Integer> current = map.get(set.findSet(i));
                if (!(strings.contains("NN") || strings.contains("PP"))) {
                    if (!depList.get(j).equals("-")) {
                        if (current.getValue() != i || !current.getKey().equals(depList.get(j))) {
                            if (!strings.contains("IN")) {
                                return false;
                            }
                        }
                    }
                    i++;
                } else {
                    if (!depList.get(j).equals("-")) {
                        if (!current.getKey().equals(depList.get(j))) {
                            return false;
                        }
                    }
                    i = current.getValue() + 1;
                }
            }
        }
        return i + 1 == sentence.length;
    }

    private static DisjointSet<String> createDisjointSet(String[] sentence, String[] dep) {
        int rootIndex = findRoot(dep);
        DisjointSet<String> set = new DisjointSet<>(sentence);
        for (int i = 0; i < dep.length; i++) {
            if (i != rootIndex) {
                int start = dep[i].indexOf("/");
                int toIndex = Integer.parseInt(dep[i].substring(start + 1)) - 1;
                if (toIndex != rootIndex) {
                    set.union(i, toIndex);
                }
            }
        }
        return set;
    }

    private static HashMap<Integer, Pair<String, Integer>> createMap(DisjointSet<String> set, String[] sentence, String[] dep) {
        int rootIndex = findRoot(dep);
        HashMap<Integer, Pair<String, Integer>> pairMap = new HashMap();
        int i = 0;
        while (i < sentence.length - 1) {
            int key = set.findSet(i);
            String pairKey = dep[i].substring(0, dep[i].indexOf("/"));
            while (i + 1 < sentence.length && set.findSet(i) == set.findSet(i + 1)) {
                i++;
                if (Integer.parseInt(dep[i].substring(dep[i].indexOf("/") + 1)) == rootIndex + 1) {
                    pairKey = dep[i].substring(0, dep[i].indexOf("/"));
                }
            }
            pairMap.put(key, new Pair<>(pairKey, i));
            i++;
        }
        return pairMap;
    }

    private static String[] editList(String[] strings) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            if (!strings[i].isEmpty()) {
                list.add(strings[i]);
            }
        }
        return list.toArray(new String[0]);
    }

    public static void main(String[] args) throws IOException {
        HashMap<Integer, String> map = new HashMap<>();
        LinkedList<LinkedList<HashSet<String>>> list = new LinkedList<>();
        LinkedList<LinkedList<String>> depList = new LinkedList<>();
        Scanner source = new Scanner(new File("automatic-qno-search-list.txt"));
        Scanner autoDependencies = new Scanner(new File("automatic-dependencies.txt"));
        int k = 0;
        while (source.hasNext()) {
            String current = source.nextLine();
            String[] autoDeps = autoDependencies.nextLine().split(" ");
            LinkedList<String> ls = new LinkedList<>();
            for (int i = 1; i < autoDeps.length - 1; i++) {
                ls.add(autoDeps[i].substring(autoDeps[i].indexOf("/") + 1));
            }
            depList.add((LinkedList<String>) ls.clone());
            list.add(new LinkedList<>());
            String[] firstSplit = current.split(" ");
            map.put(k, firstSplit[0]);
            for (int i = 1; i < firstSplit.length; i++) {
                String s = firstSplit[i];
                list.getLast().add(new HashSet<>());
                String[] secondSplit = s.split("/");
                for (String value : secondSplit) {
                    list.getLast().getLast().add(value);
                }
            }
            k++;
        }
        BufferedWriter outfile;
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("dependency-output.txt"), StandardCharsets.UTF_8);
        outfile = new BufferedWriter(writer);
        Scanner dependencySource = new Scanner(new File("dependency.txt"));
        Scanner sentenceSource = new Scanner(new File("dependency-sentences.txt"));
        int m = 0;
        while (dependencySource.hasNext()) {
            String dependencies = dependencySource.nextLine();
            String[] sentence = editList(sentenceSource.nextLine().split(" "));
            try {
                if (!dependencies.isEmpty()) {
                    String[] dep = dependencies.split(" ");
                    DisjointSet<String> set = createDisjointSet(sentence, dep);
                    HashMap<Integer, Pair<String, Integer>> map1 = createMap(set,sentence, dep);
                    for (int i = 0; i < list.size(); i++) {
                        if (isMatch(sentence, set, depList.get(i), map1, list.get(i))) {
                            outfile.write(map.get(i));
                            break;
                        }
                    }
                }
                System.out.println((m + 1) + ". done.");
            } catch (Exception e) {
                System.out.println((m + 1) + ". not done.");
            }
            outfile.newLine();
            m++;
        }
        outfile.close();
    }
}
