package jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A utility class for managing database connection properties.
 * This class provides methods to load and retrieve database configuration
 * from a properties file.
 * 
 * @author ND
 * @version 1.0
 * @since 2024-11-01
 */
public class DatabaseProperties {
    /**
     * Properties object to store the database configuration.
     * Initialized as a static field to ensure single instance across the
     * application.
     */
    private static Properties properties = new Properties();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseProperties() {
        // Prevent instantiation
    }

    /**
     * Loads database properties from a specified properties file.
     * 
     * @param filename The path to the properties file to be loaded
     * @throws RuntimeException if the file cannot be found or read
     */
    public static void load(String filename) {
        try (FileInputStream file = new FileInputStream(filename)) {
            properties.load(file);
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
        }
    }

    /**
     * Loads database properties from the default properties file.
     * The default file name is "database.properties".
     * 
     * @throws RuntimeException if the default properties file cannot be found or
     *                          read
     */
    public static void load() {
        load("database.properties");
    }

    /**
     * Retrieves the database URL from the properties file.
     * 
     * @return the database URL as a String
     * @throws IllegalStateException if properties haven't been loaded or the URL is
     *                               not defined
     */
    public static String getUrl() {
        String url = properties.getProperty("db.url");
        if (url == null) {
            throw new IllegalStateException("Database URL not found in properties file");
        }
        return url;
    }

    /**
     * Retrieves the database username from the properties file.
     * 
     * @return the database username as a String
     * @throws IllegalStateException if properties haven't been loaded or the
     *                               username is not defined
     */
    public static String getUser() {
        String user = properties.getProperty("db.user");
        if (user == null) {
            throw new IllegalStateException("Database user not found in properties file");
        }
        return user;
    }

    /**
     * Retrieves the database password from the properties file.
     * 
     * @return the database password as a String
     * @throws IllegalStateException if properties haven't been loaded or the
     *                               password is not defined
     */
    public static String getPassword() {
        String password = properties.getProperty("db.password");
        if (password == null) {
            throw new IllegalStateException("Database password not found in properties file");
        }
        return password;
    }
}