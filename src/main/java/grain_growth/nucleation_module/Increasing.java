package grain_growth.nucleation_module;

import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

/**
 * Created by jerin on 8/10/19
 */
public class Increasing extends NucleationModule {

    @Override
    public void addNucleons(Growth growth, Grid grid) {

        nucleating.nucleating(growth, grid, numberOfGrains);
        numberOfGrains += increasing;
    }

    @Override
    public String toString() {
        return "Increasing";
    }
}
