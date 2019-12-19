package grain_growth.structure;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

import java.util.List;

/**
 * Created by jerin on 8/10/19
 */
public interface Structure {

    List<Cell> selectGrains(Growth growth, Grid grid, int numberOfStructures);
}
