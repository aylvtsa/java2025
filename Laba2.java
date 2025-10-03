import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ЛР2: Із заданого тексту видалити всі слова визначеної довжини,
 * що починаються з приголосної літери.
 */
public final class Laba2 {

    /** Розділювач для виводу. */
    private static final String SEP = "-*".repeat(80);

    /** Голосні літери  */
    private static final String VOWELS = "аеєиіїоуюяaeiou";

    /** Патерн для знаходження слова  */
    private static final Pattern WORD_PATTERN =
            Pattern.compile("\\b\\p{L}+(?:['’]\\p{L}+)*\\b", Pattern.UNICODE_CHARACTER_CLASS);

    private Laba2() {}

    public static void main(String[] args) {
        run();
    }

    /**
     * Зчитування вхідних даних від користувача (довжину слова, текст),
     * обробка тексту, друк результату
     */
    public static void run() {
        try (Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println(SEP);
            System.out.print("Введіть довжину слова (ціле > 0): ");

            final String lenRaw = sc.nextLine().trim();
            final int targetLen = Integer.parseInt(lenRaw);

            if (targetLen <= 0) {
                throw new IllegalArgumentException("Довжина має бути > 0");
            }

            System.out.println("Введіть текст (порожній рядок — завершити введення):");
            final StringBuilder inputBuilder = new StringBuilder();

            // Зчитування тексту до порожнього рядка
            while (sc.hasNextLine()) {
                final String line = sc.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                inputBuilder.append(line).append(System.lineSeparator());
            }

            // Видалення зайвих пробілів/розділювачів на початку/кінці введеного тексту
            final String text = inputBuilder.toString().strip();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Порожній текст");
            }

            // Обробка тексту
            final Matcher matcher = WORD_PATTERN.matcher(text);
            final StringBuilder outputBuilder = new StringBuilder(text.length());

            int lastMatchEnd = 0;
            int removedCount = 0;

            while (matcher.find()) {
                final String word = matcher.group();
                // Використання codePointCount для коректної довжини Unicode-слова
                final int wordLen = word.codePointCount(0, word.length());
                final int firstCodePoint = word.codePointAt(0);

                // Перевірка, чи слово починається з приголосної літери
                final boolean startsWithConsonant = isConsonant(firstCodePoint);
                final boolean shouldRemove = startsWithConsonant && (wordLen == targetLen);

                if (shouldRemove) {
                    // Додаємо текст між попереднім і поточним збігом
                    outputBuilder.append(text, lastMatchEnd, matcher.start());
                    lastMatchEnd = matcher.end();
                    removedCount++;
                }
            }
            // Додаємо залишок тексту після останнього збігу
            outputBuilder.append(text, lastMatchEnd, text.length());

            // Фінальне очищення тексту:
            // 1. Заміна послідовностей пробілів/табуляцій на один пробіл.
            // 2. Видалення пробілів/табуляцій наприкінці рядків.
            // 3. Видалення пробілів на початку/кінці всього тексту.
            final String cleaned = outputBuilder.toString()
                    .replaceAll("[\\t ]{2,}", " ")
                    .replaceAll("(?m)[\\t ]+$", "")
                    .strip();

            // Вивід
            System.out.println(SEP);
            System.out.println("Вхідний текст");
            System.out.println(text);
            System.out.println("\nДовжина для видалення: " + targetLen);
            System.out.println("Видалено слів: " + removedCount);

            System.out.println(SEP);
            System.out.println("Результат");
            System.out.println(cleaned);

        } catch (NumberFormatException e) {
            System.err.println("Помилка: довжина має бути цілим числом.");
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка вхідних даних: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непередбачена помилка: " + e.getMessage());
        }
    }

    /**
     * Перевірка чи є задана кодова точка приголосною літерою
     * @param codePoint Кодова точка Unicode першого символу слова
     * @return  true — приголосна літера, інакше false
     */
    private static boolean isConsonant(int codePoint) {
        if (!Character.isLetter(codePoint)) {
            return false;
        }
        final int lowerCaseCodePoint = Character.toLowerCase(codePoint);
        return VOWELS.indexOf(lowerCaseCodePoint) < 0;
    }
}
