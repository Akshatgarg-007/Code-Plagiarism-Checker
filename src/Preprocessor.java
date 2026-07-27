import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {

    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
        // ── Language keywords ──
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double",
        "else", "enum", "extends", "final", "finally", "float", "for",
        "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private",
        "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while",
        // ── Literals ──
        "true", "false", "null",
        // ── Primitive type wrappers & common types ──
        "string", "integer", "character", "boolean", "object", "system",
        "math", "arrays", "collections", "list", "arraylist", "linkedlist",
        "map", "hashmap", "treemap", "set", "hashset", "treeset",
        "queue", "deque", "stack", "vector", "iterator", "comparable",
        "comparator", "iterable", "runnable", "thread", "exception",
        "runtimeexception", "throwable", "error", "override",
        // ── I/O & common standard library ──
        "scanner", "file", "inputstream", "outputstream", "reader",
        "writer", "bufferedreader", "bufferedwriter", "printwriter",
        "printstream", "filereader", "filewriter",
        // ── Common method names (standard API) ──
        "main", "println", "print", "printf", "format",
        "equals", "hashcode", "tostring", "compareto",
        "length", "size", "get", "put", "add", "remove", "contains",
        "isempty", "clear", "toarray", "valueof", "parseint",
        "parsedouble", "parselong", "parsefloat",
        "nextint", "nextline", "nextdouble", "next", "hasnext",
        "close", "read", "write", "flush", "append",
        // ── Placeholder tokens (from earlier normalization steps) ──
        "num", "str", "id"
    ));

    private static final Pattern SINGLE_LINE_COMMENT =
            Pattern.compile("//[^\\n]*");

    private static final Pattern MULTI_LINE_COMMENT =
            Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private static final Pattern STRING_LITERAL =
            Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");

    private static final Pattern CHAR_LITERAL =
            Pattern.compile("'(?:[^'\\\\]|\\\\.)*'");

    private static final Pattern NUMERIC_LITERAL =
            Pattern.compile(
                "\\b0[xX][0-9a-fA-F_]+[lL]?\\b"       
              + "|\\b0[bB][01_]+[lL]?\\b"               
              + "|\\b\\d[\\d_]*\\.?[\\d_]*(?:[eE][+-]?\\d+)?[fFdDlL]?\\b" 
              + "|\\.\\d[\\d_]*(?:[eE][+-]?\\d+)?[fFdD]?\\b"              
            );

    private static final Pattern IDENTIFIER =
            Pattern.compile("\\b([a-zA-Z_$][a-zA-Z0-9_$]*)\\b");

    private static final Pattern WHITESPACE_RUN =
            Pattern.compile("\\s+");


    /**
     * @param rawSource 
     * @return 
     */
    public static String preprocess(String rawSource) {
        if (rawSource == null || rawSource.isEmpty()) {
            return "";
        }

        String code = rawSource;

        code = MULTI_LINE_COMMENT.matcher(code).replaceAll(" ");
        code = SINGLE_LINE_COMMENT.matcher(code).replaceAll(" ");

        code = STRING_LITERAL.matcher(code).replaceAll("\"STR\"");

        code = CHAR_LITERAL.matcher(code).replaceAll("'C'");

        code = NUMERIC_LITERAL.matcher(code).replaceAll("NUM");

        code = code.toLowerCase();

        code = normalizeIdentifiers(code);

        code = WHITESPACE_RUN.matcher(code).replaceAll(" ").trim();

        return code;
    }

    /**
     * @param lowercasedCode 
     * @return 
     */
    private static String normalizeIdentifiers(String lowercasedCode) {
        Matcher matcher = IDENTIFIER.matcher(lowercasedCode);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String ident = matcher.group(1);
            if (JAVA_KEYWORDS.contains(ident)) {
                matcher.appendReplacement(sb, ident);  
            } else {
                matcher.appendReplacement(sb, "ID");   
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param rawSource   
     * @param cleanedCode the output of {@link #preprocess(String)}
     * @return 
     */
    public static String summarize(String rawSource, String cleanedCode) {
        int rawTokens    = rawSource.split("\\s+").length;
        int cleanTokens  = cleanedCode.split("\\s+").length;
        double reduction = rawTokens == 0 ? 0 :
                (1.0 - (double) cleanTokens / rawTokens) * 100.0;

        return String.format(
            "  Raw tokens: %,d → Cleaned tokens: %,d  (%.1f%% reduction)",
            rawTokens, cleanTokens, reduction
        );
    }
}
