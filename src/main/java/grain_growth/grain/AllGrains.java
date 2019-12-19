package grain_growth.grain;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

import java.util.List;

/**
 * Created by jerin on 08/10/19
 */
public class AllGrains implements GrainSelection {

    @Override
    public List<Cell> selectEdgeGrains(Growth growth, Grid grid, int numberOfGrains) {

        List<Cell> edgeCells = grid.getEdgeCells();

        grid.forEachCells(cell -> {
            if (edgeCells.contains(cell)) {
                cell.setState(Cell.INCLUSION_STATE);
            } else if (cell.getState() != Cell.INCLUSION_STATE) {
                cell.setState(Cell.INITIALIZE_STATE);
            }
        });

        return grid.getCells();
    }

    @Override
    public String toString() {
        return "All grains";
    }
}
