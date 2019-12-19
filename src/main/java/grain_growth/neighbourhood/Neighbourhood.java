package grain_growth.neighbourhood;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;

import java.util.List;

/**
 * Created by jerin on 8/10/19
 */
public abstract class Neighbourhood implements Neighbours {

    protected List<Cell> cells;

    public abstract List<Cell> listWithNeighbours(Grid grid, Cell cell);

    @Override
    public void addCellToNeighboursList(Cell cell) {

        if (cell != null){
            cells.add(cell);
        }
    }
}
