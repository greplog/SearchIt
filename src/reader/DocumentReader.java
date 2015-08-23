package reader;

import com.google.common.base.Splitter;
import indexes.InMemoryIndexes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static indexes.InMemoryIndexes.documentToId;
import static indexes.InMemoryIndexes.stopWords;

public class DocumentReader {

    private File file;
    private static ExecutorService threads = Executors.newFixedThreadPool(100);

    public DocumentReader(File file) {
        this.file = file;
    }


    public void readDocument() throws IOException, InterruptedException {
//        System.out.println("start " + new Date());
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        List<Callable<Object>> tasks = new ArrayList<>();
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                tasks.add(Executors.callable(new Tokenizer(line, file.getPath())));
//                tokenizeLine(line, file.getPath());
            }
            threads.invokeAll(tasks);
        } finally {
//            System.out.println("end " + new Date() + " tasks " + tasks.size());
            LineIterator.closeQuietly(it);
        }
    }


    private void tokenizeLine(String line, String fileName) {
        Integer documentId = documentToId.get(fileName);
        for (String str : Splitter.onPattern("\\s+").split(line)) {
            String prunedStr = pruning(str);
            if(!isStopWord(prunedStr)) {
                Map<String, Set<Integer>> map = InMemoryIndexes.preIndex
                    .getOrDefault(preIndexKey(prunedStr), new ConcurrentHashMap<>());
                Set<Integer> set = map.getOrDefault(prunedStr, new HashSet<>());
                set.add(documentId);
                map.put(prunedStr, set);
                InMemoryIndexes.preIndex.put(preIndexKey(prunedStr), map);
            }
        }
    }

    public static boolean isStopWord(String str){
        if(stopWords.contains(str)){
            return true;
        }
        return false;
    }

    public static String pruning(String str){
        return str.toLowerCase();
    }

    private Integer preIndexKey(String str) {
        return str.charAt(0) % 131;
    }

}
