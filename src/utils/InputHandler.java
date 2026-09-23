package utils;

import exception.InvalidInputException;

import java.util.Scanner;

public class InputHandler {

    private static final Scanner s = new Scanner(System.in);

    public static int getNumericValue(String label) throws Exception {

        while (true) {
            System.out.println("Enter your "+label+":");
            try {
                int value =  Integer.parseInt(s.nextLine());
                if (value < 0) throw new InvalidInputException("Enter positive value");
                return value;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Enter numeric input");
            } catch (Exception e) {
                throw new Exception("Unexpected Error Occurred"+e.getMessage());
            }
        }
    }

    public static String getStringValue(String lable) throws Exception {
        while (true) {
            System.out.println("Enter your "+lable+":");
            try {
                String value = s.nextLine().trim();
                if (value.isEmpty()) throw new InvalidInputException("Enter Non empty String value");
                return value;
            } catch (Exception e) {
                throw new Exception("Unexpected Error Occurred"+e.getMessage());
            }
        }
    }
}
