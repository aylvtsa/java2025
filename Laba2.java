import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Laba2{
    private Laba2() {}

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try (Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("-*" .repeat(80));
            System.out.print("Введіть довжину слова (ціле > 0): ");
            String lenRaw = sc.nextLine().trim();
            int targetLen = Integer.parseInt(lenRaw);
            if (targetLen <= 0) {
                throw new IllegalArgumentException("Довжина має бути > 0");
            }

            System.out.println("Введіть текст (порожній рядок — завершити введення):");
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (line.isEmpty()) break;
                sb.append(line).append('\n');
            }
            String text = sb.toString().trim();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Порожній текст");
            }

            RemovalResult res = removeWords(text, targetLen);

            System.out.println("-*" .repeat(80));
            System.out.println("Вхідний текст ");
            System.out.println(text);
            System.out.println("\nДовжина для видалення: " + targetLen);
            System.out.println("Видалено слів: " + res.removedCount);

            System.out.println("-*" .repeat(80));
            System.out.println("Результат ");
            System.out.println(res.text);

        } catch (NumberFormatException e) {
            System.err.println("Помилка: довжина має бути цілим числом.");
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка вхідних даних: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непередбачена помилка: " + e.getMessage());
        }
    }

    private static final class RemovalResult {
        final String text;
        final int removedCount;
        RemovalResult(String text, int removedCount) {
            this.text = text;
            this.removedCount = removedCount;
        }
    }

    private static RemovalResult removeWords(String text, int length) {

        Pattern WORD = Pattern.compile("\\b\\p{L}+\\b", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = WORD.matcher(text);

        StringBuilder out = new StringBuilder(text.length());
        int last = 0, removed = 0;

        while (m.find()) {
            String word = m.group();
            int wordLen = word.codePointCount(0, word.length());
            boolean startsWithConsonant = isConsonant(word.codePointAt(0));
            boolean shouldRemove = startsWithConsonant && (wordLen == length);

            if (shouldRemove) {
                out.append(text, last, m.start());
                last = m.end();
                removed++;
            }
        }
        out.append(text.substring(last));

        String cleaned = out.toString()
                .replaceAll("[ \\t]{2,}", " ")
                .trim();

        return new RemovalResult(cleaned, removed);
    }

    private static boolean isConsonant(int codePoint) {
        if (!Character.isLetter(codePoint)) return false;
        int lower = Character.toLowerCase(codePoint);
        return !isVowel(lower);
    }
    private static boolean isVowel(int lowerCaseCodePoint) {
        final String vowels = "аеєиіїоуюяaeiouy";
        return vowels.indexOf(lowerCaseCodePoint) >= 0;
    }
}
