package indexes;


import entities.Position;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIndexes {
    public static Map<Integer, Map<String, Set<Integer>>> preIndex = new ConcurrentHashMap<>();
    public static Map<Integer, String> idToDocument = new HashMap<>();
    public static Map<String, Integer> documentToId = new HashMap<>();
    public static Set<String> stopWords = new HashSet<>();
    public static Integer numOfDocuments = 0;
    public static Map<String, Map<Integer, List<Position>>> fileIndex = new ConcurrentHashMap<>();
    public static Map<String, Integer> wordFrequency = new ConcurrentHashMap<>();
    public static Map<String, Object> wordLock = new ConcurrentHashMap<>();
    public static final Integer THRESHOLD_FOR_SERIALIZING = 1000*1000;

    static {
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(new File("./stoplist.txt"), "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                stopWords.add(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
    }
}
