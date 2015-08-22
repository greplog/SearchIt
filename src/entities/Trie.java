package entities;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Trie {
    public TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word, Integer docId) {
        HashMap<Character, TrieNode> children = root.children;
        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);
            TrieNode t;
            t = children.getOrDefault(c, new TrieNode(c));
            children.put(c, t);
            children = t.children;
            if(i==word.length()-1) {
                t.isLeaf = true;
                if(t.docIds == null){
                    t.docIds = new HashSet<>();
                }
                t.docIds.add(docId);
            }
        }
    }

    public boolean search(String word) {
        TrieNode t = searchNode(word);
        if(t != null && t.isLeaf)
            return true;
        else
            return false;
    }

    public boolean startsWith(String prefix) {
        if(searchNode(prefix) == null)
            return false;
        else
            return true;
    }

    public Set<Integer> getDocIds(String str){
        TrieNode trieNode = searchNode(str);
        return trieNode.docIds;
    }

    public TrieNode searchNode(String str){
        Map<Character, TrieNode> children = root.children;
        TrieNode t = null;
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);
            if(children.containsKey(c)){
                t = children.get(c);
                children = t.children;
            }else{
                return null;
            }
        }
        return t;
    }
}
