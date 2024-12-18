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
            pstmtUser.setInt(1, personId); // Use pstmtUser, not pstmtPerson
            pstmtUser.setTimestamp(2, userData.getRegistrationDate()); // Set the correct timestamp
            pstmtUser.executeUpdate();

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
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        pstmt.setInt(3, stationId);

            // Execute query and display results
        try (ResultSet rs = pstmt.executeQuery()) {
                UI.printResults(rs);
            }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error while listing orders: " + e.getMessage());
    } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
        System.out.println("Invalid input. Ensure the parameters are start date, and end date, station ID.");
    }
}

    
    public static void listReplacementOrders(Timestamp startDate, Timestamp endDate, int stationId) throws SQLException {
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
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        pstmt.setInt(3, stationId);

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
        final String UPDATE_DOCK = "UPDATE dock SET state = 'free', scooter = NULL WHERE number = ?";
        final String INSERT_TRAVEL = "INSERT INTO travel (dtinitial, client, scooter, stinitial) VALUES (?, ?, ?, ?)";
        final String CHECK_SCOOTER = """
                    SELECT dock.number, dock.state 
                    FROM dock 
                    WHERE dock.scooter = ? AND dock.station = ?
                """;
    
        try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement pstmtScooter = conn.prepareStatement(CHECK_SCOOTER);
             PreparedStatement pstmtDock = conn.prepareStatement(UPDATE_DOCK);
             PreparedStatement pstmtTravel = conn.prepareStatement(INSERT_TRAVEL)) {
    
            conn.setAutoCommit(false); // Begin transaction
    
            // Step 1: Check if the scooter exists and is available at the station
            System.out.println("Checking scooter availability... ");
            pstmtScooter.setInt(1, scooterId);
            pstmtScooter.setInt(2, stationId);
    
            int dockId = -1; // Default dock ID
            try (ResultSet rs = pstmtScooter.executeQuery()) {
                if (rs.next()) {
                    dockId = rs.getInt("number");
                    String dockState = rs.getString("state");
                    System.out.println("Dock found: ID = " + dockId + ", State = " + dockState);
    
                    // Adjusted condition to match 'occupy' as the valid state
                    if (!"occupy".equalsIgnoreCase(dockState)) {
                        throw new SQLException("Scooter not available for travel. Current state: " + dockState);
                    }
                } else {
                    throw new SQLException("No scooter found at the specified station with ID: " + scooterId);
                }
            }
    
            // Step 2: Update the dock to free the scooter
            System.out.println("Freeing up dock with ID: " + dockId);
            pstmtDock.setInt(1, dockId);
            int rowsUpdated = pstmtDock.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update dock. Dock ID: " + dockId);
            }
    
            // Step 3: Insert a new travel record
            System.out.println("Inserting new travel record for Client ID: " + clientId + ", Scooter ID: " + scooterId);
            pstmtTravel.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Current timestamp
            pstmtTravel.setInt(2, clientId);
            pstmtTravel.setInt(3, scooterId);
            pstmtTravel.setInt(4, stationId);
    
            pstmtTravel.executeUpdate();
            conn.commit(); // Commit the transaction
    
            System.out.println("Travel started successfully for Client ID: " + clientId + ", Scooter ID: " + scooterId);
    
        } catch (SQLException e) {
            System.out.println("Error in startTravel: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Error starting travel: " + e.getMessage());
        }
    }
    
    
    

    public class Restriction {
        public static int findFreeDock(Connection conn, int stationId) throws SQLException {
            final String QUERY = """
                SELECT number
                FROM dock
                WHERE station = ? AND state = 'free'
                LIMIT 1
            """;
    
            try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
                stmt.setInt(1, stationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("number");
                    } else {
                        throw new SQLException("No free docks available at the station.");
                    }
                }
            }
        }
    }
    
    

    
    public static void stopTravel(int clientId, int scooterId, int stationId) throws SQLException {
        final String UPDATE_TRAVEL = """
            UPDATE travel 
            SET dtfinal = ?, stfinal = ? 
            WHERE client = ? AND scooter = ? AND dtfinal IS NULL
        """;
        final String UPDATE_DOCK = "UPDATE dock SET state = 'occupied', scooter = ? WHERE number = ?";
        final String UPDATE_CREDIT = """
            UPDATE card 
            SET credit = GREATEST(credit - ?, 0) 
            WHERE client = ?
        """;
        final String GET_TRAVEL_DETAILS = """
            SELECT dtinitial 
            FROM travel 
            WHERE client = ? AND scooter = ? AND dtfinal IS NULL
        """;
    
        try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement updateTravelStmt = conn.prepareStatement(UPDATE_TRAVEL);
             PreparedStatement updateDockStmt = conn.prepareStatement(UPDATE_DOCK);
             PreparedStatement updateCreditStmt = conn.prepareStatement(UPDATE_CREDIT);
             PreparedStatement getTravelDetailsStmt = conn.prepareStatement(GET_TRAVEL_DETAILS)) {
    
            conn.setAutoCommit(false);
    
            // Update travel record with end details
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            updateTravelStmt.setTimestamp(1, currentTime);
            updateTravelStmt.setInt(2, stationId);
            updateTravelStmt.setInt(3, clientId);
            updateTravelStmt.setInt(4, scooterId);
    
            if (updateTravelStmt.executeUpdate() == 0) {
                throw new SQLException("No active travel found for this client and scooter.");
            }
    
            // Fetch travel start time
            Timestamp startTime;
            getTravelDetailsStmt.setInt(1, clientId);
            getTravelDetailsStmt.setInt(2, scooterId);
            try (ResultSet rs = getTravelDetailsStmt.executeQuery()) {
                if (rs.next()) {
                    startTime = rs.getTimestamp("dtinitial");
                } else {
                    throw new SQLException("Travel start time not found.");
                }
            }
    
            // Calculate travel cost
            long travelDuration = (currentTime.getTime() - startTime.getTime()) / (60 * 1000);
            double travelCost = 1 + (travelDuration * 0.15);
    
            updateCreditStmt.setDouble(1, travelCost);
            updateCreditStmt.setInt(2, clientId);
            if (updateCreditStmt.executeUpdate() == 0) {
                throw new SQLException("Failed to update credit balance.");
            }
    
            // Find and update dock
            int dockId = Restriction.findFreeDock(conn, stationId);
            updateDockStmt.setInt(1, scooterId);
            updateDockStmt.setInt(2, dockId);
            updateDockStmt.executeUpdate();
    
            conn.commit();
            System.out.println("Travel ended successfully. Total cost: " + travelCost + " euros.");
        } catch (SQLException e) {
            throw new SQLException("Error ending travel: " + e.getMessage(), e);
        }
    }
    
    

    public static void updateDocks(String query,String state, int dockNumber,int stationId) {
        System.out.println("updateDocks()");
        query = "UPDATE DOCK SET state = ?, scooter = ? WHERE number = ? AND station = ?";
    
            try 
                (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(query)){    
                Integer scooterId = null;
                pstmt.setInt(4,stationId);
                if ("occupy".equals(state)) {
                    System.out.println("Enter scooter ID");
                    Scanner scanner = new Scanner(System.in);
                    scooterId = scanner.nextInt();
                }
                pstmt.setString(1, state);
                if (scooterId == null) {
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(2, scooterId);
                }
                pstmt.setInt(3, dockNumber);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("No dock found with the specified details.");
                }
            }
            catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void userSatisfaction() throws SQLException {
        // SQL query to calculate average rating, total trips, and satisfaction percentage
        final String sql = """
            SELECT sm.designation AS model_name, 
                   AVG(t.evaluation) AS average_rating, 
                   COUNT(t.dtinitial) AS total_journeys,
                   (SUM(CASE WHEN t.evaluation >= 4 THEN 1 ELSE 0 END) * 100.0 / COUNT(t.dtinitial)) AS satisfaction_rate
            FROM travel t
            INNER JOIN scooter s ON t.scooter = s.id
            INNER JOIN scootermodel sm ON s.model = sm.number
            WHERE t.evaluation IS NOT NULL
            GROUP BY sm.designation
            ORDER BY average_rating DESC
        """;
    
        // Attempt to connect to the database and execute the query
        try (Connection connection = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            // Execute query and retrieve results
            try (ResultSet results = statement.executeQuery()) {
                // Print results in a formatted way
                System.out.println("Satisfaction Report:");
                UI.printResults(results);
            }
        } catch (SQLException e) {
            // Handle SQL errors gracefully
            System.err.println("An error occurred while fetching user satisfaction data.");
            e.printStackTrace();
        }
    }
    

    public static void occupationStation() throws SQLException {
        // SQL query to retrieve top 3 stations with the highest dock occupation
        final String sql = """
            SELECT st.id AS station_id,
                   COUNT(d.number) AS total_docks,
                   SUM(CASE WHEN d.state = 'occupied' THEN 1 ELSE 0 END) AS occupied_docks
            FROM station st
            INNER JOIN dock d ON st.id = d.station
            GROUP BY st.id
            ORDER BY occupied_docks DESC
            LIMIT 3;
        """;
    
        try (Connection connection = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            // Execute query
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Top 3 Stations by Occupied Docks:");
                // Format and print the results
                UI.printResults(resultSet);
            }
        } catch (SQLException e) {
            // Display error message and stack trace
            System.err.println("Error retrieving station occupation data.");
            e.printStackTrace();
        }
    }
    
}