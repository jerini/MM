package grain_growth.growth;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.neighbourhood.Neighbourhood;

/**
 * Created by jerin on 08/10/19
 */
public interface Algorithm {

    void initialize(Grid grid);
    void mark(Grid grid, Neighbourhood neighbourhood);
    void changeState(Cell cell);
}
