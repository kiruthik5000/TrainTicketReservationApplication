package utils;

import exception.InvalidInputException;

import java.util.Scanner;

public class InputHandler {

    private static final Scanner s = new Scanner(System.in);

    public static int getNumericValue(String label, int limit) {

        while (true) {
            System.out.println("Enter your "+label+":");
            try {
                int value =  Integer.parseInt(s.nextLine());
                if (value < 0 || value > limit) throw new InvalidInputException("Enter positive value");
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter numeric Input"+e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected Error Occurred"+e.getMessage());
            }
        }
    }

    public static String getStringValue(String label) {
        while (true) {
            System.out.println("Enter your "+ label +":");
            try {
                String value = s.nextLine().trim();
                if (value.isEmpty()) throw new InvalidInputException("Enter Non empty String value");
                return value;
            } catch (Exception e) {
                System.out.println("Unexpected Error Occurred"+e.getMessage());
            }
        }
    }
}
