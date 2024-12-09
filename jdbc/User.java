package jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * User class that extends Person, representing a user in the system
 */
public class User extends Person {
    private Timestamp registrationDate;

    /**
     * Default constructor
     * Initializes a new User with current timestamp
     */
    public User() {
        super();
        this.registrationDate = new Timestamp(new Date().getTime());
    }

    /**
     * Constructor with basic user information
     * 
     * @param email     User's email
     * @param taxNumber User's tax number
     * @param name      User's name
     */
    public User(String email, int taxNumber, String name) {
        super(email, taxNumber, name);
        this.registrationDate = new Timestamp(new Date().getTime());
    }

    /**
     * Constructor with all user information including registration date
     * 
     * @param email     User's email
     * @param taxNumber User's tax number
     * @param name      User's name
     * @param date      Registration date
     */
    public User(String email, int taxNumber, String name, LocalDateTime date) {
        super(email, taxNumber, name);
        this.registrationDate = date != null ? Timestamp.valueOf(date) : new Timestamp(new Date().getTime());
    }

    /**
     * Constructor from string array
     * 
     * @param attr Array containing user attributes [email, taxNumber, name]
     * @throws IllegalArgumentException if array is null or has incorrect length
     * @throws NumberFormatException    if taxNumber cannot be parsed
     */
    public User(String[] attr) {
        super(attr[0], Integer.parseInt(attr[1]), attr[2]);
        this.registrationDate = new Timestamp(new Date().getTime());
    }

    /**
     * Get the registration date
     * 
     * @return Timestamp of registration
     */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Set the registration date using LocalDateTime
     * 
     * @param date New registration date
     */
    public void setRegistrationDate(LocalDateTime date) {
        this.registrationDate = date != null ? Timestamp.valueOf(date) : null;
    }

    /**
     * Set the registration date using Timestamp
     * 
     * @param timestamp New registration date
     */
    public void setRegistrationDate(Timestamp timestamp) {
        this.registrationDate = timestamp;
    }

    /**
     * Returns a string representation of the User
     * 
     * @return String representation of the User
     */
    @Override
    public String toString() {
        return String.format("User{email='%s', taxNumber=%d, name='%s', registrationDate=%s}",
                getEmail(), getTaxNumber(), getName(), registrationDate);
    }

    /**
     * Checks if two User objects are equal
     * 
     * @param obj Object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        User user = (User) obj;
        return registrationDate != null ? registrationDate.equals(user.registrationDate)
                : user.registrationDate == null;
    }

    /**
     * Generates hashCode for the User
     * 
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        return result;
    }
}