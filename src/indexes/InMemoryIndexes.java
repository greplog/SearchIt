package indexes;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryIndexes {
    public static Map<Integer, Map<String, Set<Integer>>> preIndex = new HashMap<>();
    public static Map<String, Set<Integer>> invertedIndex = new HashMap<>();
    public static Map<Integer, String> idToDocument = new HashMap<>();
    public static Map<String, Integer> documentToId = new HashMap<>();
    public static Integer numOfDocuments = 0;
}
