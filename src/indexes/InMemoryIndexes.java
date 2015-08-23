package indexes;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIndexes {
    public static Map<Integer, Map<String, Set<Integer>>> preIndex = new ConcurrentHashMap<>();
    public static Map<Integer, String> idToDocument = new HashMap<>();
    public static Map<String, Integer> documentToId = new HashMap<>();
    public static Set<String> stopWords = new HashSet<>();
    public static Integer numOfDocuments = 0;

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
