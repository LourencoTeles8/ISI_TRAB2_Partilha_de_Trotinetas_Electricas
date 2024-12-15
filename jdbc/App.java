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
        for (int i = 1; i <= smd.getColumnCount(); i++)
            System.out.format("%-15s", smd.getColumnLabel(i));
        // Horizontal line, be carefully with line size
        StringBuffer sep = new StringBuffer("\n");
        for (int j = 0; j < 2 * (smd.getColumnCount() + TAB_SIZE); j++)
            sep.append('-');
        System.out.println(sep);
        // Print results
        try {
            while (dr.next()) {
                for (int i = 1; i <= smd.getColumnCount(); i++)
                    System.out.format("%-15s", dr.getObject(i));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Invalid arguments: " + e.getMessage());
        }
        // TODO
        /*
         * Result must be similar like:
         * ListDepartment()
         * dname dnumber mgrssn mgrstartdate
         * -----------------------------------------------------
         * Research 5 333445555 1988-05-22
         * Administration 4 987654321 1995-01-01
         */
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

    private void startStopTravel()  {
        System.out.println("startStopTravel()");
        try {
            String travelDetails = Model.inputData("Enter travel details (operation, client ID, station ID, scooter ID):\n");
            String[] details = travelDetails.split(",");
            Model.travel(details);
            System.out.println("Travel operation completed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error processing travel operation.");
        }
    }

    private void updateDocks() {
        System.out.println("updateDocks()");
        try {
            String dockDetails = Model.inputData("Enter dock details (dock number, station ID, state, scooter ID):\n");
            String[] details = dockDetails.split(",");
            int dockNumber = Integer.parseInt(details[0]);
            int stationId = Integer.parseInt(details[1]);
            String state = details[2];
            Integer scooterId = details.length > 3 ? Integer.parseInt(details[3]) : null;
    
            String query = "UPDATE DOCK SET state = ?, scooter = ? WHERE number = ? AND station = ?";
            Model.updateDocks(query, state, scooterId, dockNumber, stationId);
            System.out.println("Dock updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error updating dock.");
        }
    }

    private void userSatisfaction() {
            System.out.println("userSatisfaction()");
            try {
                String satisfactionDetails = Model.inputData("Enter user satisfaction details (user ID, rating, comments):\n");
                String[] details = satisfactionDetails.split(",");
                int userId = Integer.parseInt(details[0]);
                int rating = Integer.parseInt(details[1]);
                String comments = details.length > 2 ? details[2] : "";
        
                String query = "INSERT INTO user_satisfaction (user_id, rating, comments) VALUES (?, ?, ?)";
                Model.userSatisfaction(query, userId, rating, comments);
                System.out.println("User satisfaction recorded successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error recording user satisfaction.");
            }
    }

    private void occupationStation() {
        System.out.println("occupationStation()");
        try {
            String stationDetails = Model.inputData("Enter station occupation details (station ID, occupation rate):\n");
            String[] details = stationDetails.split(",");
            int stationId = Integer.parseInt(details[0]);
            double occupationRate = Double.parseDouble(details[1]);
    
            String query = "UPDATE station SET occupation_rate = ? WHERE id = ?";
            Model.occupationStation(query, occupationRate, stationId);
            System.out.println("Station occupation updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error updating station occupation.");
        }
}

public class App {

    public static void main(String[] args) throws Exception {
        DatabaseProperties.load();
        String url = String.format("%s?user=%s&password=%s&ssl=false", DatabaseProperties.getUrl(),
                DatabaseProperties.getUser(), DatabaseProperties.getPassword());

            UI.getInstance().setConnectionString(url);
            UI.getInstance().Run();
        }
    }
}