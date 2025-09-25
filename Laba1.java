import java.util.Arrays;

public final class Laba1 {
    private Laba1() {}

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try {
            double[][] A = {
                    {1.0,  2.5, -3.0, 4.0},
                    {0.0, -1.5,  2.0, 8.0},
                    {7.0,  3.5,  0.5, 1.0}
            };
            double[][] B = {
                    {2.0,  1.5,  5.0, 0.0},
                    {9.0, -1.5, -2.0, -3.0},
                    {1.0,  6.5,  0.5, 2.0}
            };

            // 1
            double[][] C = xorByBits(A, B);
            System.out.println("-*".repeat(50));
            System.out.println("Матриця C = A XOR B :");
            printMatrix(C);
            System.out.println("-*".repeat(50));

            // 2
            double sumMaxEvenCols = sumOfColumnExtremes(C, true,  true);
            double sumMinOddCols  = sumOfColumnExtremes(C, false, false);
            System.out.printf("Σ(макс у парних стовпцях) = %.6f%n", sumMaxEvenCols);
            System.out.printf("Σ(мін у непарних стовпцях) = %.6f%n", sumMinOddCols);

        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    // A XOR B
    private static double[][] xorByBits(double[][] A, double[][] B) {
        requireSameShape(A, B);
        int n = A.length, m = A[0].length;
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                long ab = Double.doubleToLongBits(A[i][j]);
                long bb = Double.doubleToLongBits(B[i][j]);
                double v = Double.longBitsToDouble(ab ^ bb);

                if (!Double.isFinite(v)) {
                    System.out.printf("Зловлено NaN/Infinity у елементі [%d,%d]. Замінено на 0.0%n", i, j);
                    v = 0.0;
                }

                C[i][j] = v;

            }
        }
        return C;
    }

    // мін/мах
    private static double sumOfColumnExtremes(double[][] M, boolean takeMax, boolean evenHumanIndex) {
        if (M == null || M.length == 0 || M[0].length == 0) throw new IllegalArgumentException("Порожня матриця");
        int r = M.length, c = M[0].length;
        double sum = 0;
        for (int j = 0; j < c; j++) {
            boolean isHumanEven = (j % 2 == 1);

            if (isHumanEven == evenHumanIndex) {
                double ex = M[0][j];
                for (int i = 1; i < r; i++) {
                    if (takeMax) {
                        ex = Math.max(ex, M[i][j]);
                    } else {
                        ex = Math.min(ex, M[i][j]);
                    }
                }
                sum += ex;
            }
        }
        return sum;
    }

    private static void requireSameShape(double[][] A, double[][] B) {
        if (A == null || B == null) throw new IllegalArgumentException("A або B == null");
        if (A.length == 0 || B.length == 0 || A[0].length == 0 || B[0].length == 0)
            throw new IllegalArgumentException("Порожні розміри");
        if (A.length != B.length || A[0].length != B[0].length)
            throw new IllegalArgumentException("Розміри A і B не збігаються");
    }

    private static void printMatrix(double[][] M) {
        for (double[] row : M) System.out.println(Arrays.toString(row));
    }
}
