import java.util.Arrays;
import java.util.Comparator;

/**
 * Демонстраційна програма для роботи з класом SportInventory.
 * Варіант 6 — Визначити клас спортивний інвентар, який складається як мінімум з 5-и полів.
 * @noinspection CallToPrintStackTrace
 */
public class Laba3 {

    public static void main(String[] args) {
        try {
            // Створюємо масив спортивного інвентаря
            SportInventory[] inventory = {
                    new SportInventory("М'яч", "Командний", 1200, 0.4, "Adidas"),
                    new SportInventory("Гантелі", "Фітнес", 1800, 10.0, "Nike"),
                    new SportInventory("Ракетка", "Теніс", 2500, 0.3, "Wilson"),
                    new SportInventory("Шолом", "Велоспорт", 2000, 0.8, "Giro"),
                    new SportInventory("М'яч", "Командний", 1500, 0.4, "Puma")
            };

            // Сортуємо: за ціною за зростанням, а при рівній ціні — за вагою за спаданням
            Arrays.sort(inventory, Comparator
                    .comparingDouble(SportInventory::getPrice)
                    .thenComparing(Comparator.comparingDouble(SportInventory::getWeight).reversed())
            );

            // Виводимо відсортований масив
            System.out.println("Відсортований масив:");
            for (SportInventory item : inventory) {
                System.out.println(item);
            }

            // Об’єкт, який потрібно знайти
            SportInventory target = new SportInventory("Ракетка", "Теніс", 2500, 0.3, "Wilson");
            System.out.println("\nОб’єкт, який потрібно знайти:");
            System.out.println(target);

            // Пошук ідентичного об’єкта
            boolean found = false;
            for (SportInventory item : inventory) {
                if (item.equals(target)) {
                    System.out.println("\nЗнайдено ідентичний об’єкт:");
                    System.out.println(item);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("\nІдентичний об’єкт не знайдено.");
            }
        } catch (Exception e) {
            // Якщо виникла будь-яка помилка — повідомляємо
            System.out.println("\n Сталася помилка під час виконання програми!");
            System.out.println("Повідомлення системи: " + e.getMessage());
            System.out.println("Тип помилки: " + e.getClass().getName());
            e.printStackTrace();
        }
    }

}

/**
 * Клас, що описує спортивний інвентар
 */
class SportInventory {
    private String name; // Назва предмета
    private String type; // Тип (наприклад, фітнес, командний)
    private double price; // Ціна, грн
    private double weight; // Вага, кг
    private String brand; // Виробник

    public SportInventory(String name, String type, double price, double weight, String brand) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.weight = weight;
        this.brand = brand;
    }

    // Гетери
    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public double getWeight() { return weight; }
    public String getBrand() { return brand; }

    // Перевизначений equals() для порівняння об'єктів
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SportInventory)) return false;
        SportInventory other = (SportInventory) obj;
        return java.util.Objects.equals(name, other.name)
                && java.util.Objects.equals(type, other.type)
                && Double.compare(weight, other.weight) == 0
                && Double.compare(price, other.price) == 0
                && java.util.Objects.equals(brand, other.brand);
    }

    // Для красивого виведення у консоль
    @Override
    public String toString() {
        return String.format("<name=%s, type=%s, price=%.2f, weight=%.1f, brand=%s>",
                name, type, price, weight, brand);
    }
}
