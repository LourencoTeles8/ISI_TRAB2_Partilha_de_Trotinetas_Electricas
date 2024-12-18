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
        final String CHECK_CREDIT = """
            SELECT credit 
            FROM card 
            WHERE client = ?
        """;
        final String CHECK_SCOOTER = """
            SELECT dock.number, dock.state 
            FROM dock 
            WHERE dock.scooter = ? AND dock.station = ?
        """;
        final String UPDATE_DOCK = "UPDATE dock SET state = 'free', scooter = NULL WHERE number = ?";
        final String INSERT_TRAVEL = "INSERT INTO travel (dtinitial, client, scooter, stinitial) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
            PreparedStatement pstmtCredit = conn.prepareStatement(CHECK_CREDIT);
            PreparedStatement pstmtScooter = conn.prepareStatement(CHECK_SCOOTER);
            PreparedStatement pstmtDock = conn.prepareStatement(UPDATE_DOCK);
            PreparedStatement pstmtTravel = conn.prepareStatement(INSERT_TRAVEL)) {
    
            conn.setAutoCommit(false); // Begin transaction
    
            // Step 1: Check user credit
            System.out.println("Checking user credit...");
            pstmtCredit.setInt(1, clientId);
            double userCredit;
            try (ResultSet rsCredit = pstmtCredit.executeQuery()) {
                if (rsCredit.next()) {
                    userCredit = rsCredit.getDouble("credit");
                    System.out.println("User credit: " + userCredit);
                    if (userCredit < 1.0) {
                        throw new SQLException("Insufficient credit. The user needs at least 1 unit to start a travel.");
                    }
                } else {
                    throw new SQLException("No card found for the specified client ID: " + clientId);
                }
            }
    
            // Step 2: Check scooter availability in the dock
            System.out.println("Checking scooter availability...");
            pstmtScooter.setInt(1, scooterId);
            pstmtScooter.setInt(2, stationId);
    
            int dockId = -1; // Initialize dockId as invalid
            try (ResultSet rsScooter = pstmtScooter.executeQuery()) {
                if (rsScooter.next()) {
                    dockId = rsScooter.getInt("number");
                    String dockState = rsScooter.getString("state");
                    System.out.println("Dock found: ID = " + dockId + ", State = " + dockState);
    
                    if (!"occupy".equalsIgnoreCase(dockState)) {
                        throw new SQLException("Scooter not available for travel. Current dock state: " + dockState);
                    }
                } else {
                    throw new SQLException("No scooter found at the specified station.");
                }
            }
    
            // Step 3: Free the dock associated with the scooter
            System.out.println("Freeing up dock with ID: " + dockId);
            pstmtDock.setInt(1, dockId);
            int rowsUpdated = pstmtDock.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update dock. Dock ID: " + dockId);
            }
    
            // Step 4: Insert the travel record
            System.out.println("Inserting new travel record...");
            pstmtTravel.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmtTravel.setInt(2, clientId);
            pstmtTravel.setInt(3, scooterId);
            pstmtTravel.setInt(4, stationId);
            pstmtTravel.executeUpdate();
    
            // Commit transaction
            conn.commit();
            System.out.println("Travel started successfully for Client ID: " + clientId + ", Scooter ID: " + scooterId);
    
            // Step 5: Update station occupation
            occupationStation();
        } catch (SQLException e) {
            throw new SQLException("Error starting travel: " + e.getMessage(), e);
        }
    }
    
    
    public class Restriction {
        public static int findFreeDock(Connection conn, int stationId) throws SQLException {
           // Consulta SQL para encontrar o primeiro dock livre em uma estação específica
        final String QUERY = """
            SELECT number
            FROM dock
            WHERE station = ? AND state = 'free'
            LIMIT 1
        """;

        // Usar try-with-resources para garantir que o PreparedStatement seja fechado automaticamente
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            // Configurar o parâmetro da consulta (ID da estação)
            stmt.setInt(1, stationId);

            // Executar a consulta e obter os resultados
            try (ResultSet rs = stmt.executeQuery()) {
                // Verificar se há algum dock disponível
                if (rs.next()) {
                    // Retornar o número do dock livre
                    return rs.getInt("number");
                } else {
                    // Lançar uma exceção caso nenhum dock esteja disponível
                    throw new SQLException("No free docks available at the station.");
                }
            }
        }
    }
    }
    
    public static void stopTravel(int clientId, int scooterId, int stationId) throws SQLException {
        // Query para atualizar a tabela `travel` com a data final da viagem (dtfinal) e a estação final (stfinal)
        final String UPDATE_TRAVEL = """
            UPDATE travel 
            SET dtfinal = ?, stfinal = ? 
            WHERE client = ? AND scooter = ? AND dtfinal IS NULL
        """;
    
        // Query para atualizar a tabela `dock`, alterando o estado para "occupy" e associando o scooter ao dock
        final String UPDATE_DOCK = "UPDATE dock SET state = 'occupy', scooter = ? WHERE number = ?";
    
        // Query para deduzir o crédito do cliente pelo custo da viagem
        final String UPDATE_CREDIT = """
            UPDATE card 
            SET credit = GREATEST(credit - ?, 0) 
            WHERE client = ?
        """;
    
        // Query para calcular a duração da viagem com base nos timestamps iniciais e finais
        final String CALCULATE_DURATION = """
            SELECT dtinitial, dtfinal
            FROM (
                SELECT dtinitial, dtfinal, ROW_NUMBER() OVER (ORDER BY dtfinal DESC) AS row_num
                FROM travel
                WHERE client = ? AND scooter = ? AND dtfinal IS NOT NULL
            ) subquery
            WHERE subquery.row_num = 1
        """;
    
        // Query para adicionar uma avaliação e comentário à viagem
        final String ADD_REVIEW = """
            UPDATE travel
            SET evaluation = ?, comment = ?
            WHERE dtinitial = (
                SELECT dtinitial
                FROM (
                    SELECT dtinitial, ROW_NUMBER() OVER (ORDER BY dtfinal DESC) AS row_num
                    FROM travel
                    WHERE client = ? AND scooter = ? AND dtfinal IS NOT NULL
                ) subquery
                WHERE subquery.row_num = 1
            )
        """;
    
        try (Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement pstmtUpdateTravel = conn.prepareStatement(UPDATE_TRAVEL);
             PreparedStatement pstmtUpdateDock = conn.prepareStatement(UPDATE_DOCK);
             PreparedStatement pstmtCalculateDuration = conn.prepareStatement(CALCULATE_DURATION);
             PreparedStatement pstmtUpdateCredit = conn.prepareStatement(UPDATE_CREDIT);
             PreparedStatement pstmtAddReview = conn.prepareStatement(ADD_REVIEW)) {
    
            // Inicia uma transação
            conn.setAutoCommit(false);
    
            // Atualiza o registro da viagem com o horário de término e estação final
            pstmtUpdateTravel.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Hora atual como dtfinal
            pstmtUpdateTravel.setInt(2, stationId); // Estação final
            pstmtUpdateTravel.setInt(3, clientId); // ID do cliente
            pstmtUpdateTravel.setInt(4, scooterId); // ID do scooter
    
            if (pstmtUpdateTravel.executeUpdate() == 0) {
                throw new SQLException("No active travel found for the given client and scooter.");
            }
    
            // Encontra um dock livre na estação de destino
            int dockNumber = Restriction.findFreeDock(conn, stationId);
    
            // Atualiza o estado do dock para "occupy" e associa o scooter
            pstmtUpdateDock.setInt(1, scooterId);
            pstmtUpdateDock.setInt(2, dockNumber);
            pstmtUpdateDock.executeUpdate();
    
            double totalCost = 0.0; // Inicializa o custo total
            pstmtCalculateDuration.setInt(1, clientId);
            pstmtCalculateDuration.setInt(2, scooterId);
    
            // Calcula a duração da viagem e o custo total
            try (ResultSet rs = pstmtCalculateDuration.executeQuery()) {
                if (rs.next()) {
                    Timestamp dtInitial = rs.getTimestamp("dtinitial");
                    Timestamp dtFinal = new Timestamp(System.currentTimeMillis());
    
                    long durationInMinutes = (dtFinal.getTime() - dtInitial.getTime()) / (60 * 1000); // Duração em minutos
                    if (durationInMinutes < 0) {
                        throw new SQLException("Invalid travel duration. Please check travel timestamps.");
                    }
    
                    totalCost = 1 + (durationInMinutes * 0.15); // Custo fixo + custo por minuto
                    System.out.println("Travel duration: " + durationInMinutes + " minutes.");
                    System.out.println("Total cost: " + totalCost + " euros.");
                } else {
                    throw new SQLException("Unable to calculate travel duration.");
                }
            }
    
            // Deduz o custo da viagem do crédito do cliente
            pstmtUpdateCredit.setDouble(1, totalCost);
            pstmtUpdateCredit.setInt(2, clientId);
    
            int rowsUpdated = pstmtUpdateCredit.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Insufficient credit to deduct travel cost.");
            }
    
            // Solicita uma avaliação do cliente
            Scanner scanner = new Scanner(System.in);
            System.out.print("Rate your travel experience from 1 to 5: ");
            int rating = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha
            System.out.print("Insert a comment based of your experience: ");
            String comment = scanner.nextLine();
    
            // Adiciona a avaliação e o comentário ao registro da viagem
            pstmtAddReview.setInt(1, rating);
            pstmtAddReview.setString(2, comment);
            pstmtAddReview.setInt(3, clientId);
            pstmtAddReview.setInt(4, scooterId);
    
            pstmtAddReview.executeUpdate();
    
            // Confirma a transação
            conn.commit();
            System.out.println("Travel stopped successfully. Total cost: " + totalCost + " euros.");
            System.out.println("Thank you for your review!");
        } catch (SQLException e) {
            throw new SQLException("Error stopping travel: " + e.getMessage(), e);
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
                occupationStation();
            } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void userSatisfaction() throws SQLException {
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
    
        try (Connection connection = DriverManager.getConnection(UI.getInstance().getConnectionString());
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            try (ResultSet results = statement.executeQuery()) {
                System.out.println("Satisfaction Report:");
                UI.printResults(results);
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while fetching user satisfaction data.");
            e.printStackTrace();
        }
    }
    

    public static void occupationStation() throws SQLException {
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
    
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Top 3 Stations by Occupied Docks:");
                UI.printResults(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving station occupation data.");
            e.printStackTrace();
        }
    }
    
}