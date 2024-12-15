package jdbc;

public class ScooterModel {
    private String designation;
    private int autonomy;

    // Constructor
    public ScooterModel(){
        designation = new String();
        autonomy = 0;
    }

    public ScooterModel(String designation, int autonomy) {
        this.designation = designation;
        this.autonomy = autonomy;
    }

    public ScooterModel(String[] attr){
        this.designation = attr[1];
        this.autonomy = Integer.parseInt(attr[2]);
    } 

    // Getters and Setters specific to ElectricScooter
    public String getDesignation() { return designation; }

    public void setDesignation(String designation) { this.designation = designation; }

    public double getAutonomy() { return autonomy; }

    public void setAutonomy(int autonomy) { this.autonomy = autonomy; }

    // Additional methods or functionality can be added as needed
}
