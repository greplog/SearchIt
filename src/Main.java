import indexes.InMemoryIndexes;
import reader.DocumentReader;
import retrievel.FetchDocuments;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static indexes.InMemoryIndexes.documentToId;
import static indexes.InMemoryIndexes.idToDocument;
import static indexes.InMemoryIndexes.numOfDocuments;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        printStatus();
        File[] files = allFiles("/Users/sachin.goyal/Downloads/datasets/");
        for(File file : files){
            if(file.isFile()){
                incrementDocCount(file.getPath());
                DocumentReader reader = new DocumentReader(file);
                reader.readDocument();
            }
        }
        FetchDocuments fetchDocuments = new FetchDocuments();
        System.out.println(String.valueOf(fetchDocuments.getDocuments("perhaps this")));
        printStatus();
    }

    private static void incrementDocCount(String fileName) {
        numOfDocuments++;
        documentToId.put(fileName, numOfDocuments);
        idToDocument.put(numOfDocuments, fileName);
    }

    public static File[] allFiles(String path){
        File folder = new File(path);
        return folder.listFiles();
    }

    public static void printStatus(){
        System.out.println(new Date() + "   " + InMemoryIndexes.documentToId.size());
        System.out.println(new Date() + "   " + InMemoryIndexes.numOfDocuments);
        System.out.println(new Date() + "   " + InMemoryIndexes.invertedIndex.size());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
