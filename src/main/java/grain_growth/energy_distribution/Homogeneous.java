package grain_growth.energy_distribution;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

/**
 * Created by jerin on 08/10/19
 */
public class Homogeneous implements EnergyDistribution {

    @Override
    public void calculateEnergy(Growth growth, Grid grid, int energyInside, int energyOnEdges) {

        grid.forEachCells(cell -> {
            if (!cell.isRecrystallized()) {
                cell.setEnergyDistribution(energyInside);
            }
        });
    }

    @Override
    public void showEnergy(Growth growth, Grid grid, int energyInside, int energyOnEdges) {

        grid.forEachCells(cell -> {
            cell.savePreviousState();
            if (cell.isRecrystallized()) {
                cell.setState(Cell.RECRYSTALLIZED_STATE);
            } else {
                cell.setState(Cell.ENERGY_INSIDE_STATE);
            }
        });
    }

    @Override
    public void showMicrostructure(Growth growth, Grid grid, int energyInside, int energyOnEdges) {
        grid.forEachCells(Cell::restorePreviousState);
    }

    @Override
    public String toString() {
        return "Homogeneous";
    }
}
