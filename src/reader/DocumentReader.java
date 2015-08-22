package reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static indexes.InMemoryIndexes.*;

public class DocumentReader{

    private File file;
    public DocumentReader(File file){
        this.file = file;
    }

    public void readDocument() throws IOException {
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                tokenizeLine(line, file.getPath());
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    private void tokenizeLine(String line, String fileName) {
            Integer documentId = documentToId.get(fileName);
            String[] tokens = line.split("\\s+");
            for (String str : tokens) {
                if(str.length() < 1) continue;
                Map<String, Set<Integer>> map = preIndex.getOrDefault(preIndexKey(str), new ConcurrentHashMap<>());
                Set<Integer> set = map.getOrDefault(str, new CopyOnWriteArraySet<>());
                set.add(documentId);
                map.put(str, set);
                preIndex.put(preIndexKey(str), map);
            }
    }

    private Integer preIndexKey(String str){
        return str.charAt(0) % 131;
    }

}
