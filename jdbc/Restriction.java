package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Restriction {

    // Method to get the restriction from the user
    public static String getRestriction() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the restriction: ");
        String restriction = scanner.nextLine();
        return restriction;
    }
}