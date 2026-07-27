import java.io.IOException;
import java.util.List;

public class Main {

    private static final double DEFAULT_THRESHOLD = 65.0;

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";

    public static void main(String[] args) {
        printBanner();

        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String filePath1 = args[0];
        String filePath2 = args[1];

        int ngramSize = Tokenizer.DEFAULT_N;
        if (args.length >= 3) {
            try {
                ngramSize = Integer.parseInt(args[2]);
                if (ngramSize < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println(RED + "  Error: N-Gram size must be a positive integer." + RESET);
                System.exit(1);
            }
        }

        double threshold = DEFAULT_THRESHOLD;
        if (args.length >= 4) {
            try {
                threshold = Double.parseDouble(args[3]);
                if (threshold < 0 || threshold > 100) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println(RED + "  Error: Threshold must be a number between 0 and 100." + RESET);
                System.exit(1);
            }
        }

        printSectionHeader("STEP 1: Reading Source Files");

        FileUtil.FileReadResult file1, file2;
        try {
            file1 = FileUtil.readFileWithMetadata(filePath1);
            System.out.println(file1);
        } catch (IOException e) {
            System.err.println(RED + "  Error reading file 1: " + e.getMessage() + RESET);
            System.exit(1);
            return;
        }

        try {
            file2 = FileUtil.readFileWithMetadata(filePath2);
            System.out.println(file2);
        } catch (IOException e) {
            System.err.println(RED + "  Error reading file 2: " + e.getMessage() + RESET);
            System.exit(1);
            return;
        }

        printSectionHeader("STEP 2: Preprocessing & Normalization");

        String cleaned1 = Preprocessor.preprocess(file1.getContent());
        String cleaned2 = Preprocessor.preprocess(file2.getContent());

        System.out.println(CYAN + "  File 1:" + RESET);
        System.out.println(Preprocessor.summarize(file1.getContent(), cleaned1));
        System.out.println(CYAN + "  File 2:" + RESET);
        System.out.println(Preprocessor.summarize(file2.getContent(), cleaned2));

        printSectionHeader("STEP 3: N-Gram Tokenization (N=" + ngramSize + ")");

        List<String> ngrams1 = Tokenizer.generateNGrams(cleaned1, ngramSize);
        List<String> ngrams2 = Tokenizer.generateNGrams(cleaned2, ngramSize);

        System.out.println(CYAN + "  File 1:" + RESET);
        System.out.println(Tokenizer.summarize(ngrams1, ngramSize));
        System.out.println(CYAN + "  File 2:" + RESET);
        System.out.println(Tokenizer.summarize(ngrams2, ngramSize));

        printSectionHeader("STEP 4: Similarity Analysis");

        SimilarityCalculator.SimilarityResult result =
                SimilarityCalculator.calculate(ngrams1, ngrams2);

        System.out.println(result);

        printSectionHeader("COMMON N-GRAMS (" + result.getCommonNGrams().size() + " distinct matches)");

        List<String> common = result.getCommonNGrams();
        if (common.isEmpty()) {
            System.out.println("  (none)");
        } else {
            int displayLimit = Math.min(common.size(), 30);
            for (int i = 0; i < displayLimit; i++) {
                System.out.println("    " + WHITE + (i + 1) + ". " + RESET + common.get(i));
            }
            if (common.size() > displayLimit) {
                System.out.println("    ... and " + (common.size() - displayLimit) + " more.");
            }
        }

        printSectionHeader("VERDICT");

        double score = result.getSimilarityPercent();
        String scoreColor = score >= threshold ? RED : GREEN;

        System.out.println();
        System.out.println(BOLD + "    ┌─────────────────────────────────────────────────┐" + RESET);
        System.out.printf( BOLD + "    │  Similarity Score:  %s%-7.2f%%%s                     │%n" + RESET,
                scoreColor, score, RESET + BOLD);
        System.out.printf( BOLD + "    │  Threshold:         %-7.2f%%                     │%n", threshold);
        System.out.println(BOLD + "    └─────────────────────────────────────────────────┘" + RESET);
        System.out.println();

        if (score >= threshold) {
            System.out.println(RED + BOLD
                + "    ⚠️  Possible Plagiarism Detected!"
                + RESET);
            System.out.println(RED
                + "    The similarity score (" + String.format("%.2f", score) + "%) meets or exceeds"
                + RESET);
            System.out.println(RED
                + "    the configured threshold (" + String.format("%.2f", threshold) + "%)."
                + RESET);
        } else {
            System.out.println(GREEN + BOLD
                + "    ✅  No Plagiarism Detected."
                + RESET);
            System.out.println(GREEN
                + "    The similarity score (" + String.format("%.2f", score) + "%) is below"
                + RESET);
            System.out.println(GREEN
                + "    the configured threshold (" + String.format("%.2f", threshold) + "%)."
                + RESET);
        }

        System.out.println();
        printDivider();
    }


    private static void printBanner() {
        System.out.println();
        System.out.println(CYAN + BOLD
            + "  ╔═══════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD
            + "  ║        JAVA CODE PLAGIARISM CHECKER  v1.0            ║" + RESET);
        System.out.println(CYAN + BOLD
            + "  ║     N-Gram  ·  Multiset Jaccard  ·  Zero-Deps       ║" + RESET);
        System.out.println(CYAN + BOLD
            + "  ╚═══════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(YELLOW + BOLD + "  ── " + title + " " + "─".repeat(Math.max(0, 50 - title.length())) + RESET);
    }

    private static void printDivider() {
        System.out.println(CYAN + "  " + "═".repeat(55) + RESET);
        System.out.println();
    }

    private static void printUsage() {
        System.out.println(YELLOW + "  Usage:" + RESET);
        System.out.println("    java Main <file1.java> <file2.java> [n-gram-size] [threshold]");
        System.out.println();
        System.out.println("  Arguments:");
        System.out.println("    file1.java    Path to the first Java source file");
        System.out.println("    file2.java    Path to the second Java source file");
        System.out.println("    n-gram-size   (Optional) Token window size, default: " + Tokenizer.DEFAULT_N);
        System.out.println("    threshold     (Optional) Plagiarism warning threshold %, default: " + DEFAULT_THRESHOLD);
        System.out.println();
        System.out.println("  Examples:");
        System.out.println("    java Main StudentA.java StudentB.java");
        System.out.println("    java Main File1.java File2.java 4 70.0");
        System.out.println();
    }
}
