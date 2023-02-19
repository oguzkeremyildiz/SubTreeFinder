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
        HashMap<Integer, String> map = new HashMap<>();
        TreeBank bank = new TreeBank(new File("Trees"));
        LinkedList<LinkedList<HashSet<String>>> list = new LinkedList<>();
        Scanner source = new Scanner(new File("find.txt"));
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
                    list.getLast().getLast().add(value.toLowerCase(new Locale("tr")));
                }
            }
            k++;
        }
        // bank.size() == 74016
        for (int i = 0; i < bank.size(); i++) {
            System.out.print(bank.get(i).getName() + ": ");
            for (int j = 0; j < list.size(); j++) {
                if (isSubTree(bank.get(i), list.get(j))) {
                    System.out.print(map.get(j));
                    break;
                }
            }
            System.out.println();
        }
    }
}