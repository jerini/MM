package grain_growth.structure;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vilgefortzz on 30/10/18
 */
public class DualPhase implements Structure {

    @Override
    public List<Cell> selectGrains(Growth growth, Grid grid, int numberOfStructures) {

        Random random = new Random();
        List<Integer> randomStates = new ArrayList<>();

        for (int i = 0; i < numberOfStructures; i++) {

            int randomState = (random.nextInt(growth.getType()) + 1);
            while (randomStates.contains(randomState)) {
                randomState = (random.nextInt(growth.getType()) + 1);
            }
            randomStates.add(randomState);
        }

        grid.forEachCells(cell -> {
            if (randomStates.contains(cell.getState())) {
                cell.setState(Cell.STRUCTURE_STATE);
            } else if (cell.getState() != Cell.INCLUSION_STATE) {
                cell.setState(Cell.INITIALIZE_STATE);
            }
        });

        return grid.getCells();
    }

    @Override
    public String toString() {
        return "Dual phase";
    }
}
