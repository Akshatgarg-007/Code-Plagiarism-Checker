# Code-Plagiarism-Checker

A **zero-dependency** Java tool that detects code plagiarism between two Java source files using **N-Gram tokenization** and **Multiset Jaccard similarity**. It's designed to catch even "lightly disguised" plagiarism — like renamed variables and methods — by normalizing identifiers before comparison.

---

## ✨ Features

- 🚫 **Zero external dependencies** — pure Java, no libraries required
- 🔤 **Smart preprocessing** — strips comments, normalizes strings/chars/numbers, and generalizes identifiers
- 🧩 **N-Gram tokenization** — sliding-window token generation (configurable size, default N=3)
- 📊 **Multiset Jaccard similarity** — robust similarity scoring that accounts for token frequency
- 🎨 **CLI with colored output** — clear ANSI-colored verdicts in the terminal
- ⚙️ **Configurable thresholds** — tune N-gram size and similarity cutoff to your needs

---

## 🏗️ How It Works

1. **Preprocessing** — Each file goes through 7 normalization steps:
   - Strip comments (single-line & multi-line)
   - Normalize string literals
   - Normalize character literals
   - Normalize numeric literals
   - Convert to lowercase
   - Normalize identifiers (non-keyword tokens → `ID`)
   - Collapse whitespace

2. **Tokenization** — The normalized source is broken into overlapping N-Grams using a sliding window (default N=3).

3. **Similarity Calculation** — Token multisets from both files are compared using:
Similarity = Σ min(countA, countB) / Σ max(countA, countB)
4. **Verdict** — If similarity exceeds the configured threshold, the tool flags the pair as a possible plagiarism match.

---

## 📁 Project Structure
java-plagiarism-checker/
├── src/
│ ├── FileUtil.java # File I/O with validation
│ ├── Preprocessor.java # 7-step source normalization
│ ├── Tokenizer.java # Sliding-window N-Gram generation
│ ├── SimilarityCalculator.java # Multiset Jaccard similarity
│ └── Main.java # CLI driver
└── samples/
├── SampleA.java # Original calculator
├── SampleB.java # Plagiarized version (renamed vars/methods)
└── SampleC.java # Unrelated program (graph BFS)

---

## 🚀 Getting Started

### Prerequisites
- Java (JDK 8 or higher)

### Compile

```bash
cd src
javac *.java
```

### Run

```bash
java Main ../samples/SampleA.java ../samples/SampleB.java
java Main ../samples/SampleA.java ../samples/SampleC.java
```

### Custom N-Gram size & threshold

```bash
java Main file1.java file2.java <ngram_size> <threshold>
```

**Example:**
```bash
java Main file1.java file2.java 4 70.0
```

---

## 🧪 Test Results

| Comparison | Similarity | Verdict |
|---|---|---|
| SampleA vs SampleB (plagiarized) | **71.20%** | ⚠️ Possible Plagiarism Detected |
| SampleA vs SampleC (unrelated) | **4.91%** | ✅ No Plagiarism Detected |

---

## 🎯 Key Design Decision: Identifier Normalization

The initial version compared identifiers as-is, which meant a plagiarized file with simply renamed variables (`a → x`, `add → sum`) scored only **14.44%** similarity — a false negative.

By normalizing all non-keyword identifiers to a generic `ID` token, the preprocessor now looks at code **structure and logic** rather than surface-level naming, correctly raising the plagiarized pair's score to **71.20%** while keeping the unrelated pair low at **4.91%**.

---

## 🔧 Configuration Options

| Parameter | Description | Default |
|---|---|---|
| `ngram_size` | Size of the sliding window for tokenization | 3 |
| `threshold` | Similarity percentage above which a match is flagged | (see CLI defaults) |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Dependencies:** None (pure standard library)

---

## 📌 Future Improvements

- [ ] Support for batch comparison across multiple files
- [ ] AST-based (structural) comparison in addition to token-based
- [ ] HTML/JSON report export
- [ ] Support for additional languages beyond Java
