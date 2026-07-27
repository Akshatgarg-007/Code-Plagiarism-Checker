import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    /**
     * @param filePath 
     * @return 
     * @throws IOException 
     */
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + path.toAbsolutePath());
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a regular file: " + path.toAbsolutePath());
        }

        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + path.toAbsolutePath());
        }

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    /**

     * @param filePath 
     * @return 
     * @throws IOException 
     */
    public static FileReadResult readFileWithMetadata(String filePath) throws IOException {
        String content = readFile(filePath);
        String fileName = Paths.get(filePath).getFileName().toString();
        return new FileReadResult(fileName, content, content.length(), content.split("\\r?\\n").length);
    }


    public static class FileReadResult {
        private final String fileName;
        private final String content;
        private final int charCount;
        private final int lineCount;

        public FileReadResult(String fileName, String content, int charCount, int lineCount) {
            this.fileName = fileName;
            this.content = content;
            this.charCount = charCount;
            this.lineCount = lineCount;
        }

        public String getFileName()  { return fileName; }
        public String getContent()   { return content; }
        public int    getCharCount() { return charCount; }
        public int    getLineCount() { return lineCount; }

        @Override
        public String toString() {
            return String.format("  %-25s %,7d chars | %,5d lines", fileName, charCount, lineCount);
        }
    }
}
