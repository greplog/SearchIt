package entities;


import java.util.HashMap;
import java.util.Set;

public class TrieNode {
    char c;
    HashMap<Character, TrieNode> children = new HashMap<>();
    boolean isLeaf;
    Set<Integer> docIds;
    public TrieNode(){}
    public TrieNode(char c){
        this.c = c;
    }
}
