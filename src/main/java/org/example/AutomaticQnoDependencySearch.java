package org.example;

import Cookies.Set.DisjointSet;

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
    private static boolean isMatch(String[] sentence, DisjointSet<String> set, LinkedList<HashSet<String>> rules) {
        int i = 0;
        for (HashSet<String> strings : rules) {
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
                if (!(strings.contains("NN") || strings.contains("PP"))) {
                    if (i + 1 < sentence.length && set.findSet(i) == set.findSet(i + 1)) {
                        if (!strings.contains("IN")) {
                            return false;
                        }
                    }
                    i++;
                } else {
                    i++;
                    while (i > 0 && i < sentence.length && set.findSet(i) == set.findSet(i - 1)) {
                        i++;
                    }
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
        Scanner source = new Scanner(new File("automatic-qno-search-list.txt"));
        int k = 0;
        while (source.hasNext()) {
            String current = source.nextLine();
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
            if (!dependencies.isEmpty()) {
                String[] dep = dependencies.split(" ");
                DisjointSet<String> set = createDisjointSet(sentence, dep);
                for (int i = 0; i < list.size(); i++) {
                    if (isMatch(sentence, set, list.get(i))) {
                        outfile.write(map.get(i));
                        break;
                    }
                }
            }
            outfile.newLine();
            System.out.println((m + 1) + ". done.");
            m++;
        }
        outfile.close();
    }
}
