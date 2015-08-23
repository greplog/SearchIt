import indexes.InMemoryIndexes;
import reader.DocumentReader;
import retrievel.FetchDocuments;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static indexes.InMemoryIndexes.*;

public class Main {

    public static boolean isAllIndexed = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        printStatus();
        List<File> files = allFiles(new File("/Users/sachin.goyal/Desktop/enron_mail_20150507.tgz.xz/maildir/"), new ArrayList<>());
        for(File file : files){
            if(file.isFile()){
                incrementDocCount(file.getPath());
                DocumentReader reader = new DocumentReader(file);
                reader.readDocument();
            }
        }
        System.out.println(new Date() + " all files indexed");
        printStatus();
        fetchDocuments();
    }

    private static void fetchDocuments(){
        FetchDocuments fetchDocuments = new FetchDocuments();
        Scanner scanner = new Scanner(System.in);
        String input;
        while((input = scanner.nextLine()) != null){
            Set<Integer> docIds = fetchDocuments.getDocuments(input);
            System.out.println(docIds.size());
            for(Integer doc : docIds) {
                System.out.println(String.valueOf(idToDocument.get(doc)) + "  " + doc);
            }
        }
    }

    private static void incrementDocCount(String fileName) {
        numOfDocuments++;
        documentToId.put(fileName, numOfDocuments);
        idToDocument.put(numOfDocuments, fileName);
    }

    public static List<File> allFiles(File folder, List<File> filesList){
        if(folder.isFile()){
            filesList.add(folder);
            return filesList;
        }
        for(File file : folder.listFiles()){
            if(file.isFile()){
                filesList.add(file);
            }else{
                allFiles(file, filesList);
            }
        }
        return filesList;
    }

    public static void printStatus(){
        System.out.println(new Date() + "   " + InMemoryIndexes.documentToId.size());
        System.out.println(new Date() + "   " + InMemoryIndexes.numOfDocuments);
        System.out.println(new Date() + "   " + InMemoryIndexes.preIndex.size());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
