package reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;

public class DocumentReader {

    private File file;
    private LineIterator it;
    private Tokenizer tokenizer = new Tokenizer();

    public DocumentReader(File file) throws IOException {
        this.file = file;
        it = FileUtils.lineIterator(file, "UTF-8");
    }


    public void readDocument() throws IOException, InterruptedException {
        int lineNumber = 0;
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                tokenizer.tokenizeLine(line, file.getPath(), lineNumber++);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

}
