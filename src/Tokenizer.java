import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tokenizer {

    public static final int DEFAULT_N = 3;

    /**
     * @param cleanedCode
     * @param n
     * @return
     * @throws IllegalArgumentException
     */
    public static List<String> generateNGrams(String cleanedCode, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N-Gram size must be >= 1, got: " + n);
        }
        if (cleanedCode == null || cleanedCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] words = cleanedCode.trim().split("\\s+");

        if (words.length < n) {
            List<String> single = new ArrayList<>(1);
            single.add(String.join(" ", words));
            return Collections.unmodifiableList(single);
        }

        List<String> ngrams = new ArrayList<>(words.length - n + 1);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= words.length - n; i++) {
            sb.setLength(0);
            for (int j = 0; j < n; j++) {
                if (j > 0)
                    sb.append(' ');
                sb.append(words[i + j]);
            }
            ngrams.add(sb.toString());
        }

        return Collections.unmodifiableList(ngrams);
    }

    /**
     * @param cleanedCode
     * @return
     */
    public static List<String> generateNGrams(String cleanedCode) {
        return generateNGrams(cleanedCode, DEFAULT_N);
    }

    /**
     * @param ngrams
     * @param n
     * @return
     */
    public static String summarize(List<String> ngrams, int n) {
        return String.format("  N-Gram size: %d | Total N-Grams generated: %,d", n, ngrams.size());
    }
}
