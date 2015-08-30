import indexes.InMemoryIndexes;
import reader.DocumentReader;
import retrievel.FetchDocuments;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static indexes.InMemoryIndexes.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        printStatus();
        List<File> files = FetchDocuments
            .allFiles(new File("/Users/sachin.goyal/Desktop/enron_mail_20150507.tgz.xz/maildir/"),
                new ArrayList<>());
        for (File file : files) {
            if (file.isFile()) {
                FetchDocuments.incrementDocCount(file.getPath());
                indexFiles(file);
            }
        }
        System.out.println(new Date() + " all files indexed");
        printStatus();
        fetchDocuments();
    }

    private static void indexFiles(File file) throws IOException, InterruptedException {
        DocumentReader reader = new DocumentReader(file);
        reader.readDocument();
        System.out.println("Files Done:" + numOfDocuments);
    }

    private static void fetchDocuments() {
        FetchDocuments fetchDocuments = new FetchDocuments();
        Scanner scanner = new Scanner(System.in);
        String input;
        while ((input = scanner.nextLine()) != null) {
            String param = scanner.nextLine();
            Set<Integer> docIds = fetchDocuments.getDocuments(input, param);
            System.out.println(docIds.size());
            for (Integer doc : docIds) {
                System.out.println(String.valueOf(idToDocument.get(doc)) + "  " + doc);
            }
        }
    }


    public static void printStatus() {
        System.out.println(new Date() + "   " + InMemoryIndexes.documentToId.size());
        System.out.println(new Date() + "   " + InMemoryIndexes.numOfDocuments);
        System.out.println(new Date() + "   " + InMemoryIndexes.fileIndex.size());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
