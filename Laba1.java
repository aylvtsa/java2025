import java.util.Arrays;

/**
 * ЛР1: 1) Матриця C = A XOR B (побітово по бінарному поданню double),
 * 2) Обчислити суму найбільших елементів у стовпцях матриці з парними номерами
 * та найменших елементів у стовпцях матриці з непарними номерами.
 */
public final class Laba1 {

    /** Розділювач для виводу. */
    private static final String SEPARATOR = "-*".repeat(50);

    private Laba1() { }

    /** Точка входу в програму */
    public static void main(String[] args) {

        final double[][] a = {
                {1.0,  2.5, -3.0, 4.0},
                {0.0, -1.5,  2.0, 8.0},
                {7.0,  3.5,  0.5, 1.0}
        };
        final double[][] b = {
                {2.0,  1.5,  5.0, 0.0},
                {9.0, -1.5, -2.0, -3.0},
                {1.0,  6.5,  0.5, 2.0}
        };

        // Перевірка розмірів
        requireSameShape(a, b);

        // Завдання 1: C = A XOR B (по бітам представлення double)
        final double[][] c = xorByBits(a, b);

        System.out.println(SEPARATOR);
        System.out.println("Матриця C = A XOR B:");
        printMatrix(c);
        System.out.println(SEPARATOR);

        // Завдання 2: обчислення сум, нумерація з одиниці
        // Сума максимумів у парних стовпцях
        final double sumMaxEvenCols = sumOfColumnExtremes(c, /*takeMax=*/true,  /*isEvenIndex=*/true);
        // Сума мінімумів у непарних стовпцях
        final double sumMinOddCols  = sumOfColumnExtremes(c, /*takeMax=*/false, /*isEvenIndex=*/false);

        System.out.printf("Σ(макс у парних стовпцях) = %.6f%n", sumMaxEvenCols);
        System.out.printf("Σ(мін у непарних стовпцях) = %.6f%n", sumMinOddCols);
        System.out.println(SEPARATOR);
    }

    /**
     * Виконує побітовий XOR елементів A та B, використовуючи їх бінарне представлення double (як long)
     *
     * @param a Перша матриця
     * @param b Друга матриця
     * @return Нова матриця C
     */
    private static double[][] xorByBits(double[][] a, double[][] b) {
        final int rows = a.length;
        final int cols = a[0].length;
        final double[][] c = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final long aBits = Double.doubleToLongBits(a[i][j]);
                final long bBits = Double.doubleToLongBits(b[i][j]);
                double result = Double.longBitsToDouble(aBits ^ bBits);

                // Обробка спеціальних значень (NaN/Inf), які можуть з'явитися після XOR — обнуляємо
                if (!Double.isFinite(result)) {
                    System.out.printf(
                            "Попередження: Зловлено NaN/Infinity у елементі C[%d,%d]. Замінено на 0.0%n", i, j);
                    result = 0.0;
                }
                c[i][j] = result;
            }
        }
        return c;
    }

    /**
     * Обчислює суму екстремумів (максимумів або мінімумів) по вибраних стовпцях, нумерація з одиниці
     *
     * @param m Матриця для обробки
     * @param takeMax true —  максимум у стовпці, false — мінімум
     * @param isEvenIndex true —  обробляються стовпці з парними індексами, false — з непарними
     * @return Сума екстремальних значень по вибраних стовпцях
     */
    private static double sumOfColumnExtremes(double[][] m, boolean takeMax, boolean isEvenIndex) {
        if (m == null || m.length == 0 || m[0].length == 0) {
            throw new IllegalArgumentException("Матриця не може бути порожньою або null.");
        }

        final int rows = m.length;
        final int cols = m[0].length;
        double sum = 0.0;

        for (int j = 0; j < cols; j++) {
            final boolean isCurrentIndexEven = ((j + 1) % 2 == 0);

            if (isCurrentIndexEven == isEvenIndex) {
                double extreme = m[0][j];
                for (int i = 1; i < rows; i++) {
                    extreme = takeMax ? Math.max(extreme, m[i][j]) : Math.min(extreme, m[i][j]);
                }
                sum += extreme;
            }
        }
        return sum;
    }

    /**
     * Перевірямщ, що матриці A та B мають однакову кількість рядків і стовпців, не є порожніми
     * @param a Перша матриця
     * @param b Друга матриця
     * @throws IllegalArgumentException Якщо матриці не відповідають вимогам
     */
    private static void requireSameShape(double[][] a, double[][] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Вхідні матриці не можуть бути null.");
        }
        // Перевірка, що матриці не мають нульових розмірів
        if (a.length == 0 || b.length == 0 || (a.length > 0 && a[0].length == 0) || (b.length > 0 && b[0].length == 0)) {
            throw new IllegalArgumentException("Матриці не можуть мати порожні розміри.");
        }
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException(
                    "Розміри матриць A і B не збігаються. A: " + a.length + "x" + a[0].length
                            + ", B: " + b.length + "x" + b[0].length);
        }
    }

    /**
     * Друкумо матрицю у консоль
     *
     * @param m Матриця для друку
     */
    private static void printMatrix(double[][] m) {
        for (double[] row : m) {
            System.out.println(Arrays.toString(row));
        }
    }
}
