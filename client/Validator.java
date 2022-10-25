package client;

import java.io.File;

public class Validator {

    private Validator() {}

    // 2000-01-01
    public static boolean isValidDate(String date) {
        if (date.length() != 10) {
            System.out.println("not a valid date! (must be yyyy-mm-dd)");
            return false;
        }
        for (int i = 0; i < date.length(); i++) {
            if (i == 4 || i == 7) {
                if (date.charAt(i) != '-') {
                    System.out.println("not a valid date! (must be yyyy-mm-dd)");
                    return false;
                } 
            } else {
                if (!Character.isDigit(date.charAt(i))) {
                    System.out.println("not a valid date! (must be yyyy-mm-dd)");
                    return false;
                } 
            }
        }
        return true;
    }

    public static boolean isValidCaption(String caption) {
        if (caption.isBlank() || caption.isEmpty()) {
            System.out.println("not a valid caption! (must not be blank or empty)");
            return false;
        }
        return true;
    }

    public static boolean isValidFilePath(String filePath) {
        if (!new File(filePath).isFile()) {
            System.out.println("not a valid file path! (must be a path to a file that exists)");
            return false;
        }
        return true;
    }
    
}