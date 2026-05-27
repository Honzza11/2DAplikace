package model;

public class AshWalker extends Player {
    private int heat;
    private static final int OVERHEAT_THRESHOLD = 10;
    private static final int OVERHEAT_DAMAGE = 5;

    public AshWalker(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
        this.heat = 0;
    }

    @Override
    public void startTurn() {
        super.startTurn();
    }

    public void endTurn() {
        if (heat > OVERHEAT_THRESHOLD) {
            System.out.println(name + " is overheating! Taking damage.");
            takeDamage(OVERHEAT_DAMAGE);
        }
    }

    @Override
    public void onCardPlayed(Card card, Entity target) {

    }

    public void addHeat(int amount) {
        this.heat += amount;
        System.out.println(name + " gains " + amount + " heat. Total: " + heat);
    }

    public void reduceHeat(int amount) {
        this.heat = Math.max(0, this.heat - amount);
        System.out.println(name + " cools down. Heat: " + heat);
    }

    public int getHeat() {
        return heat;
    }

    public int getDamageBonus() {
        return heat / 2;
    }

    public void resetHeat() {
        heat = 0;
    }
}
