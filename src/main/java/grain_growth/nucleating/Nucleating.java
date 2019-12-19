package grain_growth.nucleating;

import grain_growth.growth.Growth;
import grain_growth.grid.Grid;

/**
 * Created by jerin on 8/10/19
 */
public interface Nucleating {

    void nucleating(Growth growth, Grid grid, int numberOfGrains);
}
