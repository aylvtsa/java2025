import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


/**
 * ЛР5: Наслідування та поліморфізм.
 *
 * <p>Програма моделює ієрархію електроприладів. Реалізовано базовий клас {@code Appliance}
 * і класи-нащадки (чайник, мікрохвильовка, телевізор тощо). Клас {@code Apartment} містить
 * колекцію приладів, уміє підраховувати споживану потужність, сортувати їх за потужністю та
 * знаходити прилади за діапазоном випромінювання. Межі діапазону вводяться користувачем із консолі.
 */

/**
 * Клас, що описує діапазон електромагнітного випромінювання (у мегагерцах).
 *
 * <p>Інваріанти:
 * <ul>
 *   <li>{@code minMHz >= 0}</li>
 *   <li>{@code maxMHz >= minMHz}</li>
 * </ul>
 */
class EmissionRange {
    /** Нижня межа (МГц). */
    public final double minMHz;
    /** Верхня межа (МГц). */
    public final double maxMHz;

    /**
     * Створює новий діапазон випромінювання.
     *
     * @param minMHz нижня межа, МГц (>= 0)
     * @param maxMHz верхня межа, МГц (>= {@code minMHz})
     * @throws IllegalArgumentException якщо межі не числа; якщо {@code minMHz < 0};
     *                                  якщо {@code maxMHz < minMHz}
     */
    EmissionRange(double minMHz, double maxMHz) {
        if (Double.isNaN(minMHz) || Double.isNaN(maxMHz))
            throw new IllegalArgumentException("Межі мають бути числами");
        if (minMHz < 0)
            throw new IllegalArgumentException("Нижня межа не може бути від’ємною");
        if (maxMHz < minMHz)
            throw new IllegalArgumentException("Верхня межа має бути більшою або рівною нижній");
        this.minMHz = minMHz;
        this.maxMHz = maxMHz;
    }

    /**
     * Перевіряє, чи перетинається поточний діапазон із запитом {@code [qMin; qMax]}.
     * Межі вважаються включними (закриті інтервали).
     *
     * @param qMin нижня межа запиту, МГц
     * @param qMax верхня межа запиту, МГц
     * @return {@code true}, якщо є перетин; інакше {@code false}
     * @throws IllegalArgumentException якщо межі запиту не числа; якщо будь-яка межа від’ємна;
     *                                  якщо {@code qMin > qMax}
     */
    boolean intersects(double qMin, double qMax) {
        if (Double.isNaN(qMin) || Double.isNaN(qMax))
            throw new IllegalArgumentException("Межі запиту мають бути числами");
        if (qMin < 0 || qMax < 0)
            throw new IllegalArgumentException("Межі запиту не можуть бути від’ємними");
        if (qMin > qMax)
            throw new IllegalArgumentException("Верхня межа має бути більшою за нижню");

        // Перетин існує, якщо проміжки не рознесені вліво/вправо.
        return !(maxMHz < qMin || minMHz > qMax);
    }

/**
     * Перевизначення equals() та hashCode()
     * (порівняння та хешування об'єктів EmissionRange за межами діапазону).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmissionRange other)) return false;
        return Double.compare(minMHz, other.minMHz) == 0
                && Double.compare(maxMHz, other.maxMHz) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minMHz, maxMHz);
    }
    
    /**
     * Людинозрозуміле представлення діапазону в МГц.
     */
    @Override
    public String toString() {
        return String.format("%.0f–%.0f МГц", minMHz, maxMHz);
    }
}

/**
 * Абстрактний клас електроприладу.
 *
 * <p>Інваріанти:
 * <ul>
 *   <li>{@code name} не порожня</li>
 *   <li>{@code powerW > 0}</li>
 *   <li>{@code emission != null}</li>
 * </ul>
 */
abstract class Appliance {
    /** Назва приладу. */
    private final String name;
    /** Номінальна потужність, Вт. */
    public final int powerW;
    /** Діапазон електромагнітного випромінювання. */
    public final EmissionRange emission;
    /** Поточний стан підключення. */
    private boolean plugged;

    /**
     * Створює прилад.
     *
     * @param name назва (не порожня)
     * @param powerW потужність, Вт (> 0)
     * @param emission діапазон випромінювання (не {@code null})
     * @throws IllegalArgumentException якщо {@code name} порожня, {@code powerW <= 0}
     *                                  або {@code emission == null}
     */
    Appliance(String name, int powerW, EmissionRange emission) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Назва не може бути порожньою");
        if (powerW <= 0)
            throw new IllegalArgumentException("Потужність має бути більшою за 0");
        if (emission == null)
            throw new IllegalArgumentException("Діапазон випромінювання не може бути null");

        this.name = name.trim();
        this.powerW = powerW;
        this.emission = emission;
        this.plugged = false;
    }

    /**
     * Повертає назву приладу.
     *
     * @return рядок із назвою 
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає номінальну потужність приладу.
     *
     * @return потужність у Вт
     */
    public int getPowerW() {
        return powerW;
    }

        /**
     * Повертає діапазон електромагнітного випромінювання приладу.
     *
     * @return об’єкт {@link EmissionRange}, що містить межі частот у МГц
     */
    public EmissionRange getEmission() {
        return emission;
    }

    /**
     * Вмикає прилад у мережу.
     *
     * @throws IllegalStateException якщо прилад уже увімкнено
     */
    public void plugIn() {
        if (plugged)
            throw new IllegalStateException(name + ": вже увімкнено!");
        plugged = true;
    }

    /**
     * Вимикає прилад з мережі.
     *
     * @throws IllegalStateException якщо прилад уже вимкнено
     */
    public void unplug() {
        if (!plugged)
            throw new IllegalStateException(name + ": вже вимкнено!");
        plugged = false;
    }

    /**
     * Поточне споживання приладу (Вт).
     *
     * @return {@code powerW}, якщо прилад увімкнено; інакше 0
     */
    public int currentPower() {
        return plugged ? powerW : 0;
    }

    /**
     * Чи підключений прилад.
     *
     * @return {@code true}, якщо увімкнено; {@code false} — якщо вимкнено
     */
    public boolean isPlugged() {
        return plugged;
    }

    /**
     *  Перевизначення equals() та hashCode()
     *  (встановлення логіки порівняння і хешування об’єктів Appliance)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appliance other)) return false;
        return powerW == other.powerW
                && Objects.equals(name, other.name)
                && Objects.equals(emission, other.emission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, powerW, emission);
    }

    /**
     * Людиночитний опис приладу.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " \"" + name + "\" (" + powerW + " Вт, " +
                emission + ", " + (plugged ? "увімкнено" : "вимкнено") + ")";
    }
}

/** Електрочайник. */
class Kettle extends Appliance {
    Kettle(String n, int p, EmissionRange e) { super(n, p, e); }
}

/** Мікрохвильова піч. */
class Microwave extends Appliance {
    Microwave(String n, int p, EmissionRange e) { super(n, p, e); }
}

/** Телевізор. */
class TV extends Appliance {
    TV(String n, int p, EmissionRange e) { super(n, p, e); }
}

/** Холодильник. */
class Fridge extends Appliance {
    Fridge(String n, int p, EmissionRange e) { super(n, p, e); }
}

/** Ноутбук. */
class Laptop extends Appliance {
    Laptop(String n, int p, EmissionRange e) { super(n, p, e); }
}


/**
 * Клас, що описує квартиру з колекцією приладів.
 */
class Apartment {
    private final List<Appliance> devices = new ArrayList<>();

    /**
     * Додає прилад до колекції.
     *
     * @param a прилад (не {@code null})
     * @throws NullPointerException якщо {@code a == null}
     */
    public void add(Appliance a) {
        devices.add(Objects.requireNonNull(a, "Прилад не може бути null"));
    }

    /**
     * Повертає немодифікований список усіх приладів.
     *
     * @return read-only список
     */
    public List<Appliance> all() {
        return Collections.unmodifiableList(devices);
    }

    /**
     * Обчислює сумарну потужність увімкнених приладів.
     *
     * @return сумарне споживання в Вт
     */
    public int totalPower() {
        int sum = 0;
        for (Appliance a : devices) sum += a.currentPower();
        return sum;
    }

    /**
     * Повертає новий список приладів, відсортований за номінальною потужністю (зростання).
     *
     * @return відсортований список (копія)
     */
    public List<Appliance> sortByPower() {
        List<Appliance> sorted = new ArrayList<>(devices);
        sorted.sort(Comparator.comparingInt(a -> a.powerW));
        return sorted;
    }

    /**
     * Знаходить прилади, чий діапазон випромінювання перетинається із запитом {@code [minMHz; maxMHz]}.
     *
     * @param minMHz нижня межа запиту, МГц
     * @param maxMHz верхня межа запиту, МГц
     * @return список приладів, що задовольняють умові
     * @throws IllegalArgumentException якщо межі запиту некоректні
     */
    public List<Appliance> findByEmission(double minMHz, double maxMHz) {
        List<Appliance> result = new ArrayList<>();
        for (Appliance a : devices)
            if (a.emission.intersects(minMHz, maxMHz)) result.add(a);
        return result;
    }
}

/**
 * Головний клас програми з введенням даних користувача.
 */
public class Laba5 {

    /**
     * Точка входу: ініціалізація даних, ввімкнення частини приладів, сортування,
     * підрахунок споживання та пошук за діапазоном, уведений користувачем.
     *
     * @param args не використовується
     */
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {

            Apartment apartment = new Apartment();

            // Додавання приладів
            apartment.add(new Kettle("Tefal", 2000, new EmissionRange(0, 5)));
            apartment.add(new Microwave("Samsung", 1500, new EmissionRange(2400, 2500)));
            apartment.add(new TV("LG", 180, new EmissionRange(470, 860)));
            apartment.add(new Kettle("Tefal", 2000, new EmissionRange(0, 5)));
            apartment.add(new Microwave("Samsung", 1500, new EmissionRange(2400, 2500)));
            apartment.add(new TV("LG", 180, new EmissionRange(470, 860)));
            apartment.add(new Fridge("Bosch", 300, new EmissionRange(0, 10)));
            apartment.add(new Laptop("HP", 90, new EmissionRange(2400, 5800)));

            // Вмикаємо частину приладів
            for (Appliance a : apartment.all()) {
                if (a.getName().equals("Tefal") || a.getName().equals("Samsung")) {
                 a.plugIn();
                }
            }

            // Сортування за потужністю
            System.out.println("\nСортування за потужністю:");
            for (Appliance a : apartment.sortByPower()) {
                System.out.println(" - " + a);
            }

            // Підрахунок споживаної потужності
            System.out.println("\nЗагальне споживання: " + apartment.totalPower() + " Вт");

            // Введення діапазону користувачем
            double min = readNonNegativeDouble(sc, "\nВведіть мінімальну межу (МГц): ");
            double max = readNonNegativeDouble(sc, "Введіть максимальну межу (МГц): ");

            // Пошук за діапазоном
            System.out.println("\nПрилади у діапазоні " + min + "–" + max + " МГц:");
            List<Appliance> found = apartment.findByEmission(min, max);

            if (found.isEmpty()) {
                System.out.println(" - Не знайдено жодного приладу у цьому діапазоні.");
            } else {
                for (Appliance a : found) {
                    System.out.println(" - " + a);
                }
            }

        } catch (IllegalArgumentException e) {
            // Помилки валідації введення або створення сутностей
            System.err.println("Помилка введення: " + e.getMessage());
        } catch (IllegalStateException e) {
            // Помилки керування станом (повторне увімкнення/вимкнення тощо)
            System.err.println("Помилка стану: " + e.getMessage());
        } catch (Exception e) {
            // Непередбачена помилка
            System.err.println("Непередбачена помилка: " + e.getMessage());
        }
    }

    /**
     * Надійне читання не від’ємного дійсного числа зі Scanner.
     * Підтримує кому, крапку як десятковий роздільник.
     *
     * @param sc     відкритий {@link Scanner}, з якого читаємо
     * @param prompt підказка для користувача
     * @return число {@code >= 0}
     */
    private static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.next().replace(',', '.');
            try {
                double v = Double.parseDouble(s);
                if (v < 0) {
                    System.err.println("Введіть число >= 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException ex) {
                System.err.println("Некоректне число. Спробуйте ще раз.");
            }
        }
    }
}
