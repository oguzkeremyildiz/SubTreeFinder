package org.example;

import ParseTree.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args) throws IOException {
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
        BufferedWriter outfile;
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("file.txt"), StandardCharsets.UTF_8);
        outfile = new BufferedWriter(writer);
        for (int i = 0; i < bank.size(); i++) {
            outfile.write(bank.get(i).getName() + ":");
            for (int j = 0; j < list.size(); j++) {
                if (isSubTree(bank.get(i), list.get(j))) {
                    outfile.write(" " + map.get(j));
                    break;
                }
            }
            outfile.newLine();
        }
        outfile.close();
    }
}