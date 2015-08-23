package reader;


import com.google.common.base.Splitter;
import indexes.InMemoryIndexes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static indexes.InMemoryIndexes.documentToId;
import static indexes.InMemoryIndexes.stopWords;

public class Tokenizer implements Runnable{

    private String str;
    private String fileName;

    public Tokenizer(String str, String fileName){
        this.str = str;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        tokenizeLine(str, fileName);
    }

    private void tokenizeLine(String line, String fileName) {
        Integer documentId = documentToId.get(fileName);
        for (String str : Splitter.onPattern("\\s+").split(line)) {
            String prunedStr = pruning(str);
            if(!isStopWord(prunedStr)) {
                int key = preIndexKey(prunedStr);
                Map<String, Set<Integer>> map = InMemoryIndexes.preIndex
                    .getOrDefault( key , new ConcurrentHashMap<>());
                Set<Integer> set = map.getOrDefault(prunedStr, new HashSet<>());
                set.add(documentId);
                map.put(prunedStr, set);
                InMemoryIndexes.preIndex.put(key , map);
            }
        }
    }

    public static boolean isStopWord(String str){
        return stopWords.contains(str);
    }

    public static String pruning(String str){
        return str.toLowerCase();
    }

    private Integer preIndexKey(String str) {
        return str.charAt(0) % 131;
    }

}
