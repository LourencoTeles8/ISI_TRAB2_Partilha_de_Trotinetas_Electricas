package jdbc;

class Scooter {
    private double weight;
    private double maxvelocity;
    private int battery;
    private ScooterModel model;
    
    // Constructors

    public Scooter() {
        weight = 0.00;
        maxvelocity = 0.00;
        battery = 0;
        model = new ScooterModel();
    }

    public Scooter(double weight, double maxvelocity, int battery, ScooterModel model) {
        this.weight = weight;
        this.maxvelocity = maxvelocity;
        this.model = model;
    }      

    // Getters and Setters
    public double getWeight() { return weight; }

    public void setWeight(double weight) { this.weight = weight; }

    public double getMaxvelocity() { return maxvelocity; }

    public void setMaxvelocity(double maxvelocity) { this.maxvelocity = maxvelocity; }

    public ScooterModel getModel() { return model; }

    public void setModelo(ScooterModel model) { this.model = model; }
}
