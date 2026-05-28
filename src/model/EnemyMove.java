package model;

public class EnemyMove {
    private String type;
    private int chanceWeight;
    private int minVal;
    private int maxVal;

    public EnemyMove() {}

    public String getType() { return type; }
    public int getChanceWeight() { return chanceWeight; }
    public int getMinVal() { return minVal; }
    public int getMaxVal() { return maxVal; }
}