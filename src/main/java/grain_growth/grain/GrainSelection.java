package grain_growth.grain;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

import java.util.List;

/**
 * Created by jerin on 08/10/19
 */
public interface GrainSelection {

    List<Cell> selectEdgeGrains(Growth growth, Grid grid, int numberOfGrains);
}
