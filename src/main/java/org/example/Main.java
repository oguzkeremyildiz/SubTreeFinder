package org.example;

import ParseTree.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static boolean dfs(ParseNode node, LinkedList<HashSet<String>> list) {
        if (node.getData().isPunctuation()) {
            return true;
        }
        if (list.isEmpty()) {
            return false;
        }
        String data = node.getData().getName().toLowerCase(new Locale("tr"));
        if (list.getFirst().contains(data)) {
            list.removeFirst();
            return true;
        }
        if (node.isLeaf()) {
            return false;
        }
        for (int i = 0; i < node.numberOfChildren(); i++) {
            ParseNode child = node.getChild(i);
            if (!dfs(child, list)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSubTree(ParseTree tree, LinkedList<HashSet<String>> list) {
        LinkedList<HashSet<String>> clone = new LinkedList<>();
        for (HashSet<String> strings : list) {
            clone.add(new HashSet<>());
            for (String key : strings) {
                clone.getLast().add(key);
            }
        }
        boolean bool = dfs(tree.getRoot(), clone);
        return clone.isEmpty() && bool;
    }

    public static void main(String[] args) throws FileNotFoundException {
        HashMap<Integer, Integer> map = new HashMap<>();
        Scanner source = new Scanner(new File("mapping.txt"));
        int k = 0;
        while (source.hasNext()) {
            int current = source.nextInt();
            map.put(k, current);
            k++;
        }
        source.close();
        TreeBank bank = new TreeBank(new File("Trees"));
        LinkedList<LinkedList<HashSet<String>>> list = new LinkedList<>();
        source = new Scanner(new File("find.txt"));
        while (source.hasNext()) {
            String current = source.nextLine();
            list.add(new LinkedList<>());
            String[] firstSplit = current.split(" ");
            for (String s : firstSplit) {
                list.getLast().add(new HashSet<>());
                String[] secondSplit = s.split("/");
                for (String value : secondSplit) {
                    list.getLast().getLast().add(value.toLowerCase(new Locale("tr")));
                }
            }
        }
        for (int i = 0; i < bank.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (isSubTree(bank.get(i), list.get(j))) {
                    System.out.println(map.get(j) + ": " + bank.get(i).toSentence());
                    break;
                }
            }
        }
    }
}