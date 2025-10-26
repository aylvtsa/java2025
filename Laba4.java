import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ЛР4:
 * Із заданого тексту видалити всі слова визначеної довжини, що починаються з приголосної літери.
 *
 * ООП-модель:
 *  - Letter     — літера (Unicode code point)
 *  - Word       — слово як масив літер
 *  - Punctuation— розділовий знак/несловесний токен
 *  - Token      — спільний інтерфейс для Word/Punctuation
 *  - Sentence   — масив токенів (склеює текст із «розумними» пробілами)
 *  - Text       — масив речень, уміє парситись зі String
 *
 * Основна логіка (видалення) реалізована у виконавчому методі run().
 * Вимога по пробілах: послідовності пробілів/табуляцій нормалізуються до одного пробілу.
 */
public final class Laba4 {

    /** Розділювач для виводу. */
    private static final String SEP = "-*".repeat(30);

    private Laba4() { }

    public static void main(String[] args) {
        run();
    }

    /**
     * Виконавчий метод — показує текст, просить довжину,
     * парсить у модель, виконує задачу та друкує результат (UTF-8)
     */
    public static void run() {
        try (Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println(SEP);

            // Фіксований текст 
            String input = """
                Сонце світить яскраво.     Гарний день для прогулянки в парку!
                Проте     4
                сильний вітер псує настрій. Хмари пливуть швидко...
                """.strip();

            System.out.println("Вхідний текст:");
            System.out.println(input);
            System.out.println(SEP);

            // Довжина слова
            System.out.print("Введіть довжину слова (ціле > 0): ");
            int targetLen = Integer.parseInt(sc.nextLine().trim());
            if (targetLen <= 0) throw new IllegalArgumentException("Довжина має бути > 0");

            Text text = Text.parse(input);

            // Видалення слів потрібної довжини, що починаються з приголосної
            int removed = text.removeWordsBy(word ->
                    word.startsWithConsonant() && word.lengthByCodePoints() == targetLen
            );

            // Вивід результату
            System.out.println(SEP);
            System.out.println("Довжина для видалення: " + targetLen);
            System.out.println("Видалено слів: " + removed);
            System.out.println(SEP);
            System.out.println("Результат:");
            System.out.println(text.toString());
            System.out.println(SEP);

        } catch (NumberFormatException e) {
            System.err.println("Помилка: довжина має бути цілим числом.");
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка вхідних даних: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непередбачена помилка: " + e.getMessage());
        }
    }
}

/* модель тексту */

/** Базовий інтерфейс токена речення: слово або розділовий знак */
interface Token {
    String asString();
    Token toUpperCase();
    Token toLowerCase();
    Token replace(String target, String replacement);
    default boolean isWord() { return false; }
}

/** Літера. Підтримує операції регістру та перевірки */
final class Letter {
    private final int codePoint;
    private static final String VOWELS = "аеєиіїоуюяaeiou";

    Letter(int codePoint) { this.codePoint = codePoint; }
    int codePoint() { return codePoint; }
    boolean isLetter() { return Character.isLetter(codePoint); }
    boolean isVowel() {
        if (!isLetter()) return false;
        int lower = Character.toLowerCase(codePoint);
        return VOWELS.indexOf(lower) >= 0;
    }
    boolean isConsonant() { return isLetter() && !isVowel(); }
    Letter toUpperCase() { return new Letter(Character.toUpperCase(codePoint)); }
    Letter toLowerCase() { return new Letter(Character.toLowerCase(codePoint)); }
    @Override public String toString() { return new String(Character.toChars(codePoint)); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Letter)) return false;
        Letter letter = (Letter) o;
        return codePoint == letter.codePoint;
    }
    @Override public int hashCode() { return Objects.hash(codePoint); }
}

/** Розділовий знак або будь-який несловесний символ */
final class Punctuation implements Token {
    private final String symbol;
    Punctuation(String symbol) { this.symbol = symbol; }
    String symbol() { return symbol; }
    @Override public String asString() { return symbol; }
    @Override public Token toUpperCase() { return this; }
    @Override public Token toLowerCase() { return this; }
    @Override public Token replace(String target, String replacement) {
        if (target == null || target.isEmpty()) return this;
        return new Punctuation(symbol.replace(target, replacement));
    }
    boolean isSentenceTerminator() { return symbol.matches("[.!?]+"); }
    @Override public String toString() { return symbol; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Punctuation)) return false;
        Punctuation that = (Punctuation) o;
        return Objects.equals(symbol, that.symbol);
    }
    @Override public int hashCode() { return Objects.hash(symbol); }
}

/** Слово як масив літер */
final class Word implements Token {
    private final List<Letter> letters;
    Word(List<Letter> letters) { this.letters = List.copyOf(letters); }
    static Word of(String raw) {
        List<Letter> list = new ArrayList<>();
        raw.codePoints().forEach(cp -> list.add(new Letter(cp)));
        return new Word(list);
    }
    int lengthByCodePoints() { return letters.size(); }
    boolean startsWithConsonant() { return !letters.isEmpty() && letters.get(0).isConsonant(); }
    @Override public String asString() {
        StringBuilder sb = new StringBuilder(letters.size());
        for (Letter l : letters) sb.append(l.toString());
        return sb.toString();
    }
    @Override public Token toUpperCase() {
        List<Letter> up = new ArrayList<>(letters.size());
        for (Letter l : letters) up.add(l.toUpperCase());
        return new Word(up);
    }
    @Override public Token toLowerCase() {
        List<Letter> low = new ArrayList<>(letters.size());
        for (Letter l : letters) low.add(l.toLowerCase());
        return new Word(low);
    }
    @Override public Token replace(String target, String replacement) {
        if (target == null || target.isEmpty()) return this;
        return Word.of(asString().replace(target, replacement));
    }
    @Override public boolean isWord() { return true; }
    @Override public String toString() { return asString(); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word word = (Word) o;
        return Objects.equals(letters, word.letters);
    }
    @Override public int hashCode() { return Objects.hash(letters); }
}

/** Речення: послідовність токенів; відповідає за «розумні» пробіли */
final class Sentence {
    private final List<Token> tokens;
    Sentence(List<Token> tokens) { this.tokens = new ArrayList<>(tokens); }
    List<Token> tokens() { return new ArrayList<>(tokens); }
    int removeWordsBy(PredicateWord predicate) {
        int removed = 0;
        Iterator<Token> it = tokens.iterator();
        while (it.hasNext()) {
            Token t = it.next();
            if (t.isWord() && predicate.test((Word) t)) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }
    Sentence toUpperCase() {
        List<Token> res = new ArrayList<>(tokens.size());
        for (Token t : tokens) res.add(t.toUpperCase());
        return new Sentence(res);
    }
    Sentence toLowerCase() {
        List<Token> res = new ArrayList<>(tokens.size());
        for (Token t : tokens) res.add(t.toLowerCase());
        return new Sentence(res);
    }
    Sentence replace(String target, String replacement) {
        List<Token> res = new ArrayList<>(tokens.size());
        for (Token t : tokens) res.add(t.replace(target, replacement));
        return new Sentence(res);
    }
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Token prev = null;
        for (Token cur : tokens) {
            String s = cur.asString();
            boolean needSpace = false;
            if (prev != null) {
                String p = prev.asString();
                boolean prevIsOpen = p.matches("[\\(\\[«\"“]");
                boolean curIsCloseOrPunct = s.matches("[,\\.!\\?:;\\)\\]»\"”]+");
                if (!prevIsOpen && !curIsCloseOrPunct) needSpace = true;
            }
            if (needSpace) sb.append(' ');
            sb.append(s);
            prev = cur;
        }
        return sb.toString();
    }
    @FunctionalInterface interface PredicateWord { boolean test(Word word); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sentence)) return false;
        Sentence sentence = (Sentence) o;
        return Objects.equals(tokens, sentence.tokens);
    }
    @Override public int hashCode() { return Objects.hash(tokens); }
}

/** Текст як масив речень, з парсером і нормалізацією пробілів/табів */
final class Text {
    private final List<Sentence> sentences;
    private static final Pattern WORD = Pattern.compile(
            "\\b\\p{L}+(?:['’]\\p{L}+)*\\b",
            Pattern.UNICODE_CHARACTER_CLASS
    );
    private static final Pattern PUNCT = Pattern.compile("[,\\.\\!\\?\\:\\;\\-\\(\\)\\[\\]«»\"“”]+");
    private static final Pattern SPACE = Pattern.compile("\\s+");

    Text(List<Sentence> sentences) { this.sentences = new ArrayList<>(sentences); }
    List<Sentence> sentences() { return new ArrayList<>(sentences); }

    static String normalizeWhitespace(String raw) {
        return raw.replaceAll("[\\t ]+", " ")
                  .replaceAll("(?m)[\\t ]+$", "")
                  .strip();
    }

    static Text parse(String raw) {
        String src = normalizeWhitespace(raw);
        List<Sentence> res = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        int i = 0;
        while (i < src.length()) {
            String tail = src.substring(i);
            Matcher mw = WORD.matcher(tail);
            Matcher mp = PUNCT.matcher(tail);
            Matcher ms = SPACE.matcher(tail);
            int wStart = mw.find() ? mw.start() : Integer.MAX_VALUE;
            int pStart = mp.find() ? mp.start() : Integer.MAX_VALUE;
            int sStart = ms.find() ? ms.start() : Integer.MAX_VALUE;
            int next = Math.min(wStart, Math.min(pStart, sStart));
            if (next == Integer.MAX_VALUE) break;
            if (next > 0) {
                current.add(new Punctuation(tail.substring(0, next)));
                i += next; continue;
            }
            if (wStart == 0) {
                String word = mw.group();
                current.add(Word.of(word));
                i += word.length();
            } else if (pStart == 0) {
                String sym = mp.group();
                Punctuation p = new Punctuation(sym);
                current.add(p);
                i += sym.length();
                if (p.isSentenceTerminator()) {
                    res.add(new Sentence(current));
                    current = new ArrayList<>();
                }
            } else {
                String sp = ms.group();
                i += sp.length();
            }
        }
        if (!current.isEmpty()) res.add(new Sentence(current));
        return new Text(res);
    }

    int removeWordsBy(Sentence.PredicateWord predicate) {
        int total = 0;
        for (Sentence s : sentences) total += s.removeWordsBy(predicate);
        return total;
    }

    Text toUpperCase() {
        List<Sentence> out = new ArrayList<>(sentences.size());
        for (Sentence s : sentences) out.add(s.toUpperCase());
        return new Text(out);
    }

    Text toLowerCase() {
        List<Sentence> out = new ArrayList<>(sentences.size());
        for (Sentence s : sentences) out.add(s.toLowerCase());
        return new Text(out);
    }

    Text replace(String target, String replacement) {
        List<Sentence> out = new ArrayList<>(sentences.size());
        for (Sentence s : sentences) out.add(s.replace(target, replacement));
        return new Text(out);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Sentence s : sentences) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(s.toString());
        }
        return normalizeWhitespace(sb.toString());
    }
}
