package jdbc;

public class Person {
    private String email;
    private int taxnumber;
    private String name;


    // Constructors
    public Person() {}

    public Person(String email, int taxnumber, String name) {
        this.email = email;
        this.taxnumber = taxnumber;
        this.name = name;
    }

    public Person(String[] attr){
       // Convert values to integer and a strings
       this.email = validateAndGetEmail(attr); 
       this.taxnumber = validateAndGetTaxNumber(attr);
       this.name = validateAndGetName(attr);  
    }  

    // Getters and Setters
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getTaxNumber() { return taxnumber; }

    public void setTaxNumber(int taxnumber) { this.taxnumber = taxnumber; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }


    // Helper methods for validation
    private static String validateAndGetEmail(String[] attr) {
        if (attr == null || attr.length < 1 || attr[0] == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (!attr[0].contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return attr[0];
    }
    
    private static int validateAndGetTaxNumber(String[] attr) {
        if (attr == null || attr.length < 2 || attr[1] == null) {
            throw new IllegalArgumentException("Tax number cannot be null");
        }
        try {
            int taxNumber = Integer.parseInt(attr[1]);
            if (taxNumber <= 0) {
                throw new IllegalArgumentException("Tax number must be positive");
            }
            return taxNumber;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid tax number format", e);
        }
    }
    
    private static String validateAndGetName(String[] attr) {
        if (attr == null || attr.length < 3 || attr[2] == null || attr[2].trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return attr[2];
    }

    // Additional methods or functionality can be added as needed


}
