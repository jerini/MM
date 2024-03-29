package grain_growth.grid;

/**
 * Created by jerin on 08/10/19
 */
public class Cell {

    public static final int RECRYSTALLIZED_STATE = -5;
    public static final int ENERGY_ON_EDGES_STATE = -4;
    public static final int ENERGY_INSIDE_STATE = -3;
    public static final int STRUCTURE_STATE = -2;
    public static final int INCLUSION_STATE = -1;
    public static final int INITIALIZE_STATE = 0;

    public static final int SQUARE_TYPE = 0;
    public static final int CIRCULAR_TYPE = 1;

    private int x;
    private int y;
    private int phase;
    private int state;
    private boolean changeable = true;

    private int energyDistribution;
    private boolean recrystallized;
    private int previousState;

    private int type = SQUARE_TYPE;

    public Cell(int x, int y) {

        this.x = x;
        this.y = y;
        this.phase = 0;
        this.state = INITIALIZE_STATE;
    }

    public Cell(int x, int y, int state) {

        this.x = x;
        this.y = y;
        this.phase = 0;
        this.state = state;
    }

    public Cell(int x, int y, int phase, int state, int type) {

        this.x = x;
        this.y = y;
        this.phase = phase;
        this.state = state;
        this.type = type;
    }

    public void savePreviousState() {
        this.previousState = this.state;
    }

    public void restorePreviousState() {
        this.state = this.previousState;
    }

    public void clearEnergyDistribution() {
        this.energyDistribution = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEnergyDistribution() {
        return energyDistribution;
    }

    public void setEnergyDistribution(int energyDistribution) {
        this.energyDistribution = energyDistribution;
    }

    public boolean isRecrystallized() {
        return recrystallized;
    }

    public void setRecrystallized(boolean recrystallized) {
        this.recrystallized = recrystallized;
    }

    public int getPreviousState() {
        return previousState;
    }

    public void setPreviousState(int previousState) {
        this.previousState = previousState;
    }
}
