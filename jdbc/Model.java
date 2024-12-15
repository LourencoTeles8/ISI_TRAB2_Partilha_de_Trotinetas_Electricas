package jdbc;

import java.util.Scanner;
import java.io.IOException;
import java.sql.*;

/*
* 
* @author MP
* @version 1.0
* @since 2024-11-07
*/
public class Model {

    static String inputData(String str) throws IOException {
        // IMPLEMENTED
        /*
         * Gets input data from user
         * 
         * @param str Description of required input values
         * 
         * @return String containing comma-separated values
         */
        Scanner key = new Scanner(System.in); // Scanner closes System.in if you call close(). Don't do it
        System.out.println("Enter corresponding values, separated by commas of:");
        System.out.println(str);
        return key.nextLine();
    }

    static public void addUser(User userData, Card cardData) {
        // PARCIALLY IMPLEMENTED / IMPLEMENTED!
        /**
         * Adds a new user with associated card to the database
         * 
         * @param userData User information
         * @param cardData Card information
         * @throws SQLException if database operation fails
         */
        final String INSERT_PERSON = "INSERT INTO person(email, taxnumber, name) VALUES (?,?,?) RETURNING id";
        final String INSERT_CARD = "INSERT INTO card(credit, typeof, client) VALUES (?,?,?)";
        final String INSERT_USER = "INSERT INTO client(person, dtregister) VALUES (?,?)";

        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmtPerson = conn.prepareStatement(INSERT_PERSON, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtCard = conn.prepareStatement(INSERT_CARD);
                PreparedStatement pstmtUser = conn.prepareStatement(INSERT_USER);) {
            conn.setAutoCommit(false);

            // Insert person
            pstmtPerson.setString(1, userData.getEmail());
            pstmtPerson.setInt(2, userData.getTaxNumber());
            pstmtPerson.setString(3, userData.getName());

            int affectedRows = pstmtPerson.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating person failed, no rows affected.");
            }

            int personId;
            try (ResultSet generatedKeys = pstmtPerson.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personId = generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException("Creating person failed, no ID obtained.");
                }
            }
            // Insert client
            pstmtPerson.setInt(1, personId);
            pstmtPerson.setTimestamp(2, userData.getRegistrationDate());
            pstmtPerson.executeUpdate();

            // Insert card
            pstmtCard.setDouble(1, cardData.getCredit());
            pstmtCard.setString(2, cardData.getReference());
            pstmtCard.setInt(3, personId);
            pstmtCard.executeUpdate();

            conn.commit();
            if (pstmtUser != null)
                pstmtUser.close();
            if (pstmtCard != null)
                pstmtCard.close();
            if (pstmtPerson != null)
                pstmtPerson.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error on insert values");
            // e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * To implement from this point forward. Do not need to change the code above.
     * -------------------------------------------------------------------------------
     * IMPORTANT:
     * --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW
     * ---
     * -------------------------------------------------------------------------------
     **/

    static void listOrders(String[] orders) {
        /** IMPLEMENTED!
         * Lists orders based on specified criteria
         * 
         * @param orders Criteria for listing orders
         * @throws SQLException if database operation fails
         */
        final String VALUE_CMD = """
        SELECT ro.dtorder, ro.dtreplacement, ro.roccupation, s.latitude, s.longitude
        FROM replacementorder ro
        JOIN station s ON ro.station = s.id
        WHERE ro.station = ? AND ro.dtorder BETWEEN ? AND ?
        ORDER BY ro.dtorder;
        """;

        try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
        PreparedStatement pstmt = conn.prepareStatement(VALUE_CMD/*QUERY??*/)) {

         // Parse input parameters
        int stationId = Integer.parseInt(orders[0]);
        Timestamp startDate = Timestamp.valueOf(orders[1]);
        Timestamp endDate = Timestamp.valueOf(orders[2]);

         // Set query parameters
        pstmt.setInt(1, stationId);
        pstmt.setTimestamp(2, startDate);
        pstmt.setTimestamp(3, endDate);

            // Execute query and display results
        try (ResultSet rs = pstmt.executeQuery()) {
            if (!rs.isBeforeFirst()) { // Check if the result set is empty
                System.out.println("No replacement orders found for the specified criteria.");
            } else {
                UI.printResults(rs);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error while listing orders: " + e.getMessage());
    } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
        System.out.println("Invalid input. Ensure the parameters are station ID, start date, and end date.");
    }
}

    
    public static void listReplacementOrders(int stationId, Timestamp startDate, Timestamp endDate) throws SQLException {
        /** IMPLEMENTED!
         * Lists replacement orders for a specific station in a given time period
         * @param stationId Station ID
         * @param startDate Start date for period
         * @param endDate End date for period
         * @throws SQLException if database operation fails
         */
        final String VALUE_CMD = """
        SELECT ro.dtorder, ro.dtreplacement, ro.roccupation, s.latitude, s.longitude
        FROM replacementorder ro
        JOIN station s ON ro.station = s.id
        WHERE ro.station = ? AND ro.dtorder BETWEEN ? AND ?
        ORDER BY ro.dtorder;
    """;

    try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
        PreparedStatement pstmt = conn.prepareStatement(VALUE_CMD)) {

        // Modifying the parameters
        pstmt.setInt(1, stationId);
        pstmt.setTimestamp(2, startDate);
        pstmt.setTimestamp(3, endDate);

        // Run the query and get the results
        try (ResultSet rs = pstmt.executeQuery()) {
            if (!rs.isBeforeFirst()) { // Checks if the resultset is empty
                System.out.println("No replacement orders found for the specified criteria.");
            } else {
                UI.printResults(rs); // Format and display the results
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error while listing replacement orders: " + e.getMessage());
    }
        System.out.print("EMPTY");
    }

    public static void travel(String[] values){
        /**
         * Processes a travel operation (start or stop)
         * @param values Array containing [operation, name, station, scooter]
         * @throws SQLException if database operation fails
         */
        try {
            // Parse inputs
            String operation = values[0].toLowerCase();
            int clientId = Integer.parseInt(values[1]);
            int stationId = Integer.parseInt(values[2]);
            int scooterId = Integer.parseInt(values[3]);
    
            // Call appropriate method based on the operation
            if ("start".equals(operation)) {
                startTravel(clientId, scooterId, stationId);
            } else if ("stop".equals(operation)) {
                stopTravel(clientId, scooterId, stationId);
            } else {
                throw new IllegalArgumentException("Invalid operation. Use 'start' or 'stop'.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing travel: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
    
    public static int getClientId(String name) throws SQLException {
        /** Auxiliar method -- if you want
         * Gets client ID by name from database
         * @param name The name of the client
         * @return client ID or -1 if not found
         * @throws SQLException if database operation fails
         */
         final String QUERY = """
        SELECT c.person
        FROM client c
        JOIN person p ON c.person = p.id
        WHERE p.name = ?
    """;

    try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
         PreparedStatement pstmt = conn.prepareStatement(QUERY)) {

        // Configure the query parameter
        pstmt.setString(1, name);

        // Executar a consulta
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // Return the customer ID found
                return rs.getInt("person");
            } else {
                // Client not found
                return -1;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error retrieving client ID: " + e.getMessage());
    }
    }

    public static void startTravel(int clientId, int scooterId, int stationId) throws SQLException {
        /**
         * Starts a new travel
         * @param clientId Client ID
         * @param scooterId Scooter ID
         * @param stationId Station ID
         * @throws SQLException if database operation fails
         */
        final String UPDATE_DOCK = "UPDATE dock SET state = 'occupy' WHERE scooter = ? AND station = ? AND state = 'free'";
    final String INSERT_TRAVEL = "INSERT INTO travel (dtinitial, client, scooter, stinitial) VALUES (CURRENT_TIMESTAMP, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
         PreparedStatement pstmtDock = conn.prepareStatement(UPDATE_DOCK);
         PreparedStatement pstmtTravel = conn.prepareStatement(INSERT_TRAVEL)) {

        conn.setAutoCommit(false); // Beginning of the transaction

        // Update Dock State
        pstmtDock.setInt(1, scooterId);
        pstmtDock.setInt(2, stationId);
        int rowsUpdated = pstmtDock.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("No free dock available for the specified scooter.");
        }

        // Insert the new trip
        pstmtTravel.setInt(1, clientId);
        pstmtTravel.setInt(2, scooterId);
        pstmtTravel.setInt(3, stationId);
        pstmtTravel.executeUpdate();

        conn.commit(); // Confirm transaction
        System.out.println("Travel started successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error starting travel: " + e.getMessage());
    }
        //System.out.print("EMPTY")
    }

    
    public static void stopTravel(int clientId, int scooterId, int stationId) throws SQLException {
        /**
         * Stops an ongoing travel
         * @param clientId Client ID
         * @param scooterId Scooter ID
         * @param stationId Destination station ID
         * @throws SQLException if database operation fails
         */
    final String UPDATE_DOCK = "UPDATE dock SET state = 'free' WHERE scooter = ? AND station = ? AND state = 'occupy'";
    final String UPDATE_TRAVEL = "UPDATE travel SET dtfinal = CURRENT_TIMESTAMP, stfinal = ? WHERE client = ? AND scooter = ? AND dtfinal IS NULL";
    final String CALCULATE_COST = """
        SELECT EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - t.dtinitial)) / 60 * sc.usable AS cost
        FROM travel t
        JOIN servicecost sc ON TRUE
        WHERE t.client = ? AND t.scooter = ? AND t.dtfinal IS NULL
    """;
    final String UPDATE_CARD_BALANCE = "UPDATE card SET credit = credit - ? WHERE client = ? AND credit >= ?";

    try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
         PreparedStatement pstmtDock = conn.prepareStatement(UPDATE_DOCK);
         PreparedStatement pstmtTravel = conn.prepareStatement(UPDATE_TRAVEL);
         PreparedStatement pstmtCost = conn.prepareStatement(CALCULATE_COST);
         PreparedStatement pstmtCard = conn.prepareStatement(UPDATE_CARD_BALANCE)) {

        conn.setAutoCommit(false); // Beginning of the transaction

        // Update the dock to release the three
        pstmtDock.setInt(1, scooterId);
        pstmtDock.setInt(2, stationId);
        int rowsUpdated = pstmtDock.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("No occupied dock found for the specified scooter.");
        }

        // Update the trip with the final station and the end time
        pstmtTravel.setInt(1, stationId);
        pstmtTravel.setInt(2, clientId);
        pstmtTravel.setInt(3, scooterId);
        rowsUpdated = pstmtTravel.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("No ongoing travel found for the specified scooter and client.");
        }

        // Calculate the cost of travel
        pstmtCost.setInt(1, clientId);
        pstmtCost.setInt(2, scooterId);
        double travelCost;
        try (ResultSet rs = pstmtCost.executeQuery()) {
            if (rs.next()) {
                travelCost = rs.getDouble("cost");
            } else {
                throw new SQLException("Failed to calculate travel cost.");
            }
        }

        // Update Customer Balance
        pstmtCard.setDouble(1, travelCost);
        pstmtCard.setInt(2, clientId);
        pstmtCard.setDouble(3, travelCost);
        rowsUpdated = pstmtCard.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("Insufficient balance to complete the travel.");
        }

        conn.commit(); // Confirm transaction
        System.out.printf("Travel ended successfully. Cost: %.2f%n", travelCost);
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error stopping travel: " + e.getMessage());
    }
        //System.out.print("EMPTY")
    }

    public static void updateDocks(String query,String state, Integer scooterID, int dockNumber,int stationId) {
        System.out.println("updateDocks()");
        try {
            String dockDetails = Model.inputData("Enter dock details (dock number, station ID, state, scooter ID):\n");
            String[] details = dockDetails.split(",");
            dockNumber = Integer.parseInt(details[0]);
            stationId = Integer.parseInt(details[1]);
            state = details[2];
            Integer scooterId = details.length > 3 ? Integer.parseInt(details[3]) : null;
    
            query = "UPDATE DOCK SET state = ?, scooter = ? WHERE number = ? AND station = ?";
    
            try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(query)) {
    
                pstmt.setString(1, state);
                if (scooterId != null) {
                    pstmt.setInt(2, scooterId);
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                pstmt.setInt(3, dockNumber);
                pstmt.setInt(4, stationId);
    
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("No dock found with the specified details.");
                }
    
                System.out.println("Dock updated successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error updating dock.");
        }
    }


    public static void userSatisfaction(String query, int userId, int rating, String comments) {
        System.out.println("userSatisfaction()");
        try {
            String satisfactionDetails = Model.inputData("Enter user satisfaction details (user ID, rating, comments):\n");
            String[] details = satisfactionDetails.split(",");
            userId = Integer.parseInt(details[0]);
            rating = Integer.parseInt(details[1]);
            comments = details.length > 2 ? details[2] : "";
    
            query = "INSERT INTO user_satisfaction (user_id, rating, comments) VALUES (?, ?, ?)";
    
            try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(query)) {
    
                pstmt.setInt(1, userId);
                pstmt.setInt(2, rating);
                pstmt.setString(3, comments);
    
                pstmt.executeUpdate();
                System.out.println("User satisfaction recorded successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error recording user satisfaction.");
        }
    }

    public static void occupationStation(String query, double occupationRate, int stationId) {
        System.out.println("occupationStation()");
        try {
            String stationDetails = Model.inputData("Enter station occupation details (station ID, occupation rate):\n");
            String[] details = stationDetails.split(",");
            stationId = Integer.parseInt(details[0]);
            occupationRate = Double.parseDouble(details[1]);
    
            query = "UPDATE station SET occupation_rate = ? WHERE id = ?";
    
            try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(query)) {
    
                pstmt.setDouble(1, occupationRate);
                pstmt.setInt(2, stationId);
    
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("No station found with the specified ID.");
                }
    
                System.out.println("Station occupation updated successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error updating station occupation.");
        }
    }
}