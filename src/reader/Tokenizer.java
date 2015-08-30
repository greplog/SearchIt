package reader;


import com.google.common.base.Splitter;
import entities.Position;
import writer.IndexSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static indexes.InMemoryIndexes.*;

public class Tokenizer implements Runnable {

    private String str;
    private String fileName;
    private int lineNumber;
    private static AtomicInteger wordCount = new AtomicInteger(0);
    private static ExecutorService executorService = Executors.newFixedThreadPool(200);

//    public Tokenizer(String str, String fileName, int lineNumber) {
//        this.str = str;
//        this.fileName = fileName;
//        this.lineNumber = lineNumber;
//    }

    @Override
    public void run() {
        tokenizeLine(str, fileName, lineNumber);
    }

    public void tokenizeLine(String line, String fileName, int lineNumber) {
        Integer documentId = documentToId.get(fileName);
        int i = 0;
        for (String str : Splitter.onPattern("\\s+").split(line)) {
            String prunedStr = pruning(str);
            if (!isStopWord(prunedStr) && prunedStr.length() > 0) {
                wordCount.incrementAndGet();
                Map<Integer, List<Position>> docToPositions =
                    fileIndex.getOrDefault(prunedStr, new ConcurrentHashMap<>());
                List<Position> list = docToPositions.getOrDefault(documentId, new ArrayList<>());
                list.add(new Position(lineNumber, i++));
                docToPositions.put(documentId, list);
                fileIndex.put(prunedStr, docToPositions);
                if(wordCount.longValue() > THRESHOLD_FOR_SERIALIZING){
                    System.out.println(wordCount.get());
                    synchronized (Tokenizer.class) {
                        serializeMap(fileIndex);
                        fileIndex = new ConcurrentHashMap<>();
                        wordCount.set(0);
                    }
                }
            }
        }
    }

    public void serializeMap(Map<String, Map<Integer, List<Position>>> index){
        for(String key : index.keySet()) {
            IndexSerializer serializer = new IndexSerializer(key, index.get(key));
            executorService.submit(serializer);
            index.remove(key);
        }
    }

    public static boolean isStopWord(String str) {
        return stopWords.contains(str);
    }

    public static String pruning(String str) {
        str = str.toLowerCase();
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);

        return m.find() ?  "" : str;
    }

}
