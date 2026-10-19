package model;

public enum SeatType {
    SLEEPER(1.0),
    AC(1.5);
    private final double fareMultiplier;
    SeatType(double fareMultiplier) {
        this.fareMultiplier = fareMultiplier;
    }
    public double getFareMultiplier() {return this.fareMultiplier;}
}
