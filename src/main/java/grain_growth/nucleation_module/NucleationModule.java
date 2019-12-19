package grain_growth.nucleation_module;

import grain_growth.grid.Grid;
import grain_growth.growth.Growth;
import grain_growth.nucleating.Nucleating;

/**
 * Created by jerin on 8/10/19
 */
public abstract class NucleationModule {

    protected int numberOfGrains;
    protected int increasing;
    protected Nucleating nucleating;

    public abstract void addNucleons(Growth growth, Grid grid);

    public int getNumberOfGrains() {
        return numberOfGrains;
    }

    public void setNumberOfGrains(int numberOfGrains) {
        this.numberOfGrains = numberOfGrains;
    }

    public int getIncreasing() {
        return increasing;
    }

    public void setIncreasing(int increasing) {
        this.increasing = increasing;
    }

    public Nucleating getNucleating() {
        return nucleating;
    }

    public void setNucleating(Nucleating nucleating) {
        this.nucleating = nucleating;
    }

    public void setProperties(int numberOfGrains, Nucleating nucleating) {
        this.numberOfGrains = numberOfGrains;
        this.increasing = numberOfGrains;
        this.nucleating = nucleating;
    }
}
