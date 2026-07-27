import java.util.Scanner;

public class SampleB {

    public static int sum(int x, int y) {
        return x + y;
    }

    public static int difference(int x, int y) {
        return x - y;
    }

    public static int product(int x, int y) {
        return x * y;
    }

    public static double quotient(int x, int y) {
        if (y == 0) {
            System.out.println("Cannot divide by zero!");
            return 0;
        }
        return (double) x / y;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Arithmetic Calculator");
        System.out.print("Enter the first value: ");
        int val1 = input.nextInt();
        System.out.print("Enter the second value: ");
        int val2 = input.nextInt();

        System.out.println("Sum: " + sum(val1, val2));
        System.out.println("Difference: " + difference(val1, val2));
        System.out.println("Product: " + product(val1, val2));
        System.out.println("Quotient: " + quotient(val1, val2));

        input.close();
    }
}
