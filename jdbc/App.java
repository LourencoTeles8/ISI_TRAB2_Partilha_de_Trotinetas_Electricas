package jdbc;

import java.sql.*;
import java.util.Scanner;
import java.util.HashMap;

interface DbWorker {
    void doWork();
}

/*
 * 
 * @author MP
 * 
 * @version 1.0
 * 
 * @since 2024-11-07
 */
class UI {
    private enum Option {
        // DO NOT CHANGE ANYTHING!
        Unknown,
        Exit,
        novelUser,
        listReplacementOrder,
        startStopTravel,
        updateDocks,
        userSatisfaction,
        occupationStation,
    }

    private static UI __instance = null;
    private String __connectionString;

    private HashMap<Option, DbWorker> __dbMethods;

    private UI() {
        // DO NOT CHANGE ANYTHING!
        __dbMethods = new HashMap<Option, DbWorker>();
        __dbMethods.put(Option.novelUser, () -> UI.this.novelUser());
        __dbMethods.put(Option.listReplacementOrder, () -> UI.this.listReplacementOrder());
        __dbMethods.put(Option.startStopTravel, () -> UI.this.startStopTravel());
        __dbMethods.put(Option.updateDocks, () -> UI.this.updateDocks());
        __dbMethods.put(Option.userSatisfaction, () -> UI.this.userSatisfaction());
        __dbMethods.put(Option.occupationStation, new DbWorker() {
            public void doWork() {
                UI.this.occupationStation();
            }
        });
    }

    public static UI getInstance() {
        if (__instance == null) {
            __instance = new UI();
        }
        return __instance;
    }

    private Option DisplayMenu() {
        Option option = Option.Unknown;
        try {
            // DO NOT CHANGE ANYTHING!
            System.out.println("Electric Scooter Sharing");
            System.out.println();
            System.out.println("1. Exit");
            System.out.println("2. Novel users");
            System.out.println("3. List of replacements order at a station over a period of time");
            System.out.println("4. Start/Stop a travel");
            System.out.println("5. Update docks' state");
            System.out.println("6. User satisfaction ratings");
            System.out.println("7. List of station");
            System.out.print(">");
            Scanner s = new Scanner(System.in);
            int result = s.nextInt();
            option = Option.values()[result];
        } catch (RuntimeException ex) {
            // nothing to do.
        }
        return option;

    }

    private static void clearConsole() throws Exception {
        for (int y = 0; y < 25; y++) // console is 80 columns and 25 lines
            System.out.println("\n");

    }

    private void Login() throws java.sql.SQLException {
        Connection con = DriverManager.getConnection(getConnectionString());
        if (con != null)
            con.close();
    }

    public void Run() throws Exception {
        Login();
        Option userInput;
        do {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try {
                __dbMethods.get(userInput).doWork();
                System.in.read();

            } catch (NullPointerException ex) {
                // Nothing to do. The option was not a valid one. Read another.
            }

        } while (userInput != Option.Exit);
    }

    public String getConnectionString() {
        return __connectionString;
    }

    public void setConnectionString(String s) {
        __connectionString = s;
    }

    /**
     * To implement from this point forward. Do not need to change the code above.
     * -------------------------------------------------------------------------------
     * IMPORTANT:
     * --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW
     * ---
     * -------------------------------------------------------------------------------
     * 
     */

    private static final int TAB_SIZE = 24;

    static void printResults(ResultSet dr) throws SQLException {
        ResultSetMetaData smd = dr.getMetaData();
        int columnCount = smd.getColumnCount();

        // Calculate column widths dynamically in one pass
        int[] columnWidths = new int[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnWidths[i - 1] = Math.max(smd.getColumnLabel(i).length(), TAB_SIZE); // Default minimum width
        }

        // Print column labels
        for (int i = 1; i <= columnCount; i++) {
            System.out.format("%-" + columnWidths[i - 1] + "s", smd.getColumnLabel(i));
        }
        System.out.println();

        // Print horizontal separator
        for (int width : columnWidths) {
            System.out.print("-".repeat(width));
            System.out.print(" "); // Space between columns
        }
        System.out.println();

        // Print rows
        while (dr.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object value = dr.getObject(i);
                System.out.format("%-" + columnWidths[i - 1] + "s", value == null ? "NULL" : value.toString());
            }
            System.out.println();
        }
    }

    private void novelUser() {
        // IMPLEMENTED
        System.out.println("novelUser()");
        try {
            String user = Model.inputData("Enter data for a new user (email, tax number, name):\n");
            String card = Model.inputData("Enter data for card acquisition (credit, reference type):\n");

            // IMPORTANT: The values entered must be separated by a comma with no blank
            // spaces, with the proper order
            User userData = new User(user.split(","));
            Card cardData = new Card(card.split(","));
            Model.addUser(userData, cardData);
            System.out.println("Inserted with success.!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void listReplacementOrder() {
        // IMPLEMENTED
        System.out.println("listReplacementOrder()");
        try {
            // IMPORTANT: The values entered must be separated by a comma with no blank
            // spaces
            String orders = Model.inputData("Enter the time interval and the station number:\n");
            Model.listOrders(orders.split(","));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startStopTravel() {
        System.out.println("Starting startStopTravel() operation...");
    
        try {
            // Request user input
            String travelDetails = Model.inputData("Enter travel details (operation, client ID, station ID, scooter ID):\n");
            String[] details = travelDetails.split(",");
    
            // Input Validation
            if (details.length != 4) {
                throw new IllegalArgumentException("Invalid input format. Expected: operation, client ID, station ID, scooter ID");
            }
    
            // Parse input
            String operation = details[0].trim().toLowerCase();
            int clientId = Integer.parseInt(details[1].trim());
            int stationId = Integer.parseInt(details[2].trim());
            int scooterId = Integer.parseInt(details[3].trim());
    
            // Process travel operation based on user input
            if ("start".equals(operation)) {
                Model.startTravel(clientId, scooterId, stationId);
                System.out.println("Travel successfully started for Client ID: " + clientId + ", Scooter ID: " + scooterId);
            } else if ("stop".equals(operation)) {
                Model.stopTravel(clientId, scooterId, stationId);
                System.out.println("Travel successfully stopped for Client ID: " + clientId + ", Scooter ID: " + scooterId);
            } else {
                throw new IllegalArgumentException("Invalid operation. Please enter 'start' or 'stop'.");
            }
    
        } catch (NumberFormatException e) {
            System.out.println("Error: Please ensure Client ID, Station ID, and Scooter ID are valid integers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    private void updateDocks() {
        System.out.println("updateDocks()");
        try {
            String dockDetails = Model.inputData("Enter dock details (dock number, station ID, state):\n");
            String[] details = dockDetails.split(",");
            int dockNumber = Integer.parseInt(details[0]);
            int stationId = Integer.parseInt(details[1]);
            String state = details[2];
    
            String query = "UPDATE DOCK SET state = ? WHERE number = ? AND station = ?";
            Model.updateDocks(query, state, dockNumber, stationId);
            System.out.println("Dock updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error updating dock.");
        }
    }

    private void userSatisfaction() {
            System.out.println("userSatisfaction()");
            try {
                Model.userSatisfaction();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    private void occupationStation() {
        System.out.println("occupationStation()");
        try {
            Model.occupationStation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

public class App {

    public static void main(String[] args) throws Exception {
        DataBaseProperties.load();
        String url = String.format("%s?user=%s&password=%s&ssl=false", DataBaseProperties.getUrl(),
                DataBaseProperties.getUser(), DataBaseProperties.getPassword());

            UI.getInstance().setConnectionString(url);
            UI.getInstance().Run();
        }
    }