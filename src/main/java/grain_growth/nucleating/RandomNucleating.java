package grain_growth.nucleating;

import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.Growth;

import java.util.Random;

/**
 * Created by jerin on 8/10/19
 */
public class RandomNucleating implements Nucleating {

    @Override
    public void nucleating(Growth growth, Grid grid, int numberOfGrains) {

        Random random = new Random();

        int x, y;
        Cell cell;

        for (int i = 0; i < numberOfGrains; i++) {

            x = random.nextInt(grid.getWidth());
            y = random.nextInt(grid.getHeight());

            cell = grid.getCell(x, y);
            growth.changeState(cell);
        }
    }

    @Override
    public String toString() {
        return "Random nucleating";
    }
}
