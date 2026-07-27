import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarityCalculator {

    /**
     * @param ngramsA
     * @param ngramsB
     * @return
     */
    public static SimilarityResult calculate(List<String> ngramsA, List<String> ngramsB) {
        Map<String, Integer> freqA = buildFrequencyMap(ngramsA);
        Map<String, Integer> freqB = buildFrequencyMap(ngramsB);

        long intersectionSize = 0;
        long unionSize = 0;

        Map<String, Boolean> allKeys = new HashMap<>();
        for (String key : freqA.keySet())
            allKeys.put(key, Boolean.TRUE);
        for (String key : freqB.keySet())
            allKeys.put(key, Boolean.TRUE);

        List<String> commonNGrams = new ArrayList<>();

        for (String ngram : allKeys.keySet()) {
            int countA = freqA.getOrDefault(ngram, 0);
            int countB = freqB.getOrDefault(ngram, 0);

            intersectionSize += Math.min(countA, countB);
            unionSize += Math.max(countA, countB);

            if (countA > 0 && countB > 0) {
                commonNGrams.add(ngram);
            }
        }

        double similarity = (unionSize == 0) ? 0.0
                : ((double) intersectionSize / unionSize) * 100.0;

        Collections.sort(commonNGrams);

        return new SimilarityResult(similarity, intersectionSize, unionSize, commonNGrams);
    }

    /**
     * @param ngrams
     * @return
     */
    private static Map<String, Integer> buildFrequencyMap(List<String> ngrams) {
        Map<String, Integer> freq = new HashMap<>();
        for (String ng : ngrams) {
            freq.merge(ng, 1, Integer::sum);
        }
        return freq;
    }

    public static class SimilarityResult {
        private final double similarityPercent;
        private final long intersectionSize;
        private final long unionSize;
        private final List<String> commonNGrams;

        public SimilarityResult(double similarityPercent, long intersectionSize,
                long unionSize, List<String> commonNGrams) {
            this.similarityPercent = similarityPercent;
            this.intersectionSize = intersectionSize;
            this.unionSize = unionSize;
            this.commonNGrams = Collections.unmodifiableList(commonNGrams);
        }

        public double getSimilarityPercent() {
            return similarityPercent;
        }

        public long getIntersectionSize() {
            return intersectionSize;
        }

        public long getUnionSize() {
            return unionSize;
        }

        public List<String> getCommonNGrams() {
            return commonNGrams;
        }

        @Override
        public String toString() {
            return String.format(
                    "  Intersection (min-sum): %,d\n" +
                            "  Union        (max-sum): %,d\n" +
                            "  Similarity:             %.2f%%",
                    intersectionSize, unionSize, similarityPercent);
        }
    }
}
