package jdbc;

public class Card {
    private String reference;
    private double credit;
    private int client;
;

    // Constructor
    public Card(){
        credit = 0.00;
        reference = new String();
        client = 0;
    }

    public Card(double credit, String reference, int client) {
        this.credit = credit;
        this.reference = reference;
        this.client = client;
    }

    public Card(double credit, String reference) {
        this.credit = credit;
        this.reference = reference;
    }

    public Card(String[] attr){
        this.credit = Double.parseDouble(attr[0]);
        this.reference = attr[1];
        if (attr.length == 3) {
            this.client = Integer.parseInt(attr[2]);
        } else {
            this.client = 0;
        }
    } 

    // Getters and Setters specific to Scooter
    public String getReference() { return reference; }

    public void setReference(String reference) { this.reference = reference; }

    public double getCredit() {return credit; }

    public void setCredit(Double credit){ this.credit = credit; }

    public int getClient() { return client; }

    public void setClient(int client){ this.client = client; }

    // Additional methods or functionality can be added as needed
}
