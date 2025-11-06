import java.util.Scanner;

class Main {

    public static int rows, columns;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("columns rows");
        String[] input = sc.nextLine().split(" ");

        columns = Integer.parseInt(input[0]);
        rows = Integer.parseInt(input[1]);

        sc.close();

        new Sketch().startApplet();
    }
}
