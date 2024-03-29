package grain_growth.energy_distribution;

import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

/**
 * Created by jerin on 08/10/19
 */
public interface EnergyDistribution {

    void calculateEnergy(Growth growth, Grid grid, int energyInside, int energyOnEdges);
    void showEnergy(Growth growth, Grid grid, int energyInside, int energyOnEdges);
    void showMicrostructure(Growth growth, Grid grid, int energyInside, int energyOnEdges);
}
