package grain_growth;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import grain_growth.energy_distribution.EnergyDistribution;
import grain_growth.energy_distribution.Heterogeneous;
import grain_growth.energy_distribution.Homogeneous;
import grain_growth.grain.AllGrains;
import grain_growth.grain.GrainSelection;
import grain_growth.grain.NGrains;
import grain_growth.grid.Cell;
import grain_growth.grid.Grid;
import grain_growth.growth.*;
import grain_growth.image.ColorGenerator;
import grain_growth.image.ImageModifier;
import grain_growth.neighbourhood.*;
import grain_growth.nucleating.Nucleating;
import grain_growth.nucleating.RandomEdgeNucleating;
import grain_growth.nucleating.RandomNucleating;
import grain_growth.nucleation_module.Constant;
import grain_growth.nucleation_module.Increasing;
import grain_growth.nucleation_module.NucleationModule;
import grain_growth.nucleation_module.SiteSaturated;
import grain_growth.structure.DualPhase;
import grain_growth.structure.Structure;
import grain_growth.structure.Substructure;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by vilgefortzz on 07/10/18
 */
public class Controller implements Initializable {

    /**
     * Paths
     */
    private final String MICROSTRUCTURES_FILES_DIR_PATH = "microstructures/files/";
    private final String MICROSTRUCTURES_IMAGES_DIR_PATH = "microstructures/images/";

    /**
     * Graphics properties
     */
    private GraphicsContext graphicsContext;

    private int width;
    private int height;

    private double cellSize;

    /**
     * Canvas
     */
    @FXML
    private Canvas canvas;

    /**
     * Growing step controller - iteration
     */
    private final StepController stepController = new StepController();
    private final int DELAY = 60;
    private int MCS;

    /**
     * Solver
     */
    private Solver solver = new Solver();

    /**
     * Grid
     */
    @FXML
    private TextField columnsText;
    @FXML
    private TextField rowsText;
    @FXML
    private CheckBox circularCheckBox;
    @FXML
    private Button generateGridButton;

    /**
     * Algorithm
     */
    @FXML
    private ComboBox<Growth> algorithmComboBox;

    /**
     * Neighbourhood
     */
    @FXML
    private ComboBox<Neighbourhood> neighbourhoodComboBox;

    /**
     * Nucleating
     */
    @FXML
    private ComboBox<Nucleating> nucleatingComboBox;
    @FXML
    private TextField grainBoundaryEnergyText;
    @FXML
    private TextField numberOfGrainsText;
    @FXML
    private Button nucleatingButton;

    /**
     * Inclusions
     */
    @FXML
    private TextField amountOfInclusionsText;
    @FXML
    private TextField sizeOfInclusionText;
    @FXML
    public ComboBox<String> typeOfInclusionComboBox;
    @FXML
    private Button addInclusionsButton;

    /**
     * Structure
     */
    @FXML
    public ComboBox<Structure> structureComboBox;
    @FXML
    private TextField numberOfStructuresText;

    /**
     * Grain selection
     */
    @FXML
    public ComboBox<GrainSelection> grainSelectionComboBox;
    @FXML
    private TextField numberOfSelectedGrainsText;
    @FXML
    private Button selectGrainsButton;

    /**
     * MC Static recrystallization
     */
    @FXML
    public ComboBox<EnergyDistribution> energyDistributionComboBox;
    @FXML
    private TextField energyInsideText;
    @FXML
    private TextField energyOnEdgesText;
    @FXML
    public ComboBox<NucleationModule> nucleationModuleComboBox;
    @FXML
    private Button calculateEnergyButton;
    @FXML
    private ToggleButton showEnergyToggleButton;

    /**
     * Start/stop growth simulation
     */
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private TextField MCSText;
    @FXML
    private TextField probabilityText;

    /**
     * Export/Import
     */
    @FXML
    private Button importFromFileButton;
    @FXML
    private Button exportToFileButton;
    @FXML
    private Button importFromBitmapButton;
    @FXML
    private Button exportToBitmapButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        graphicsContext = canvas.getGraphicsContext2D();

        width = (int)canvas.getWidth();
        height = (int)canvas.getHeight();

        initializeOptions();
    }

    private void initializeOptions() {

        algorithmComboBox.getItems().addAll(
                new SimpleGrainGrowth(),
                new BoundaryShapeControlGrainGrowth(),
                new MonteCarloGrainGrowth(),
                new MonteCarloStaticRecrystallization()
        );

        neighbourhoodComboBox.getItems().addAll(
                new VonNeumann(),
                new Moore(),
                new NearestMoore(),
                new FurtherMoore()
        );

        nucleatingComboBox.getItems().addAll(
                new RandomNucleating(),
                new RandomEdgeNucleating()
        );

        typeOfInclusionComboBox.getItems().addAll(
                "square",
                "circular"
        );

        structureComboBox.getItems().addAll(
                null,
                new Substructure(),
                new DualPhase()
        );

        grainSelectionComboBox.getItems().addAll(
                new AllGrains(),
                new NGrains()
        );

        energyDistributionComboBox.getItems().addAll(
                new Homogeneous(),
                new Heterogeneous()
        );

        nucleationModuleComboBox.getItems().addAll(
                new SiteSaturated(),
                new Constant(),
                new Increasing()
        );

        algorithmComboBox.getSelectionModel().selectFirst();
        neighbourhoodComboBox.getSelectionModel().selectFirst();
        nucleatingComboBox.getSelectionModel().selectFirst();
        typeOfInclusionComboBox.getSelectionModel().selectFirst();
        grainSelectionComboBox.getSelectionModel().selectFirst();
        energyDistributionComboBox.getSelectionModel().selectFirst();
        nucleationModuleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void generateGrid() throws Exception {

        int columns = Integer.parseInt(columnsText.getText());
        int rows = Integer.parseInt(rowsText.getText());
        List<Cell> cells = null;
        Structure structure = structureComboBox.getSelectionModel().getSelectedItem();

        if (structure != null && solver.getGrowth() != null && solver.getGrid() != null) {
            int numberOfStructures = Integer.parseInt(numberOfStructuresText.getText());
            solver.setStructure(structure);
            cells = solver.selectGrains(numberOfStructures);
        }

        generateGrid(columns, rows, cells, circularCheckBox.isSelected());
    }

    private void generateGrid(int columns, int rows, List<Cell> cells, boolean isCircular) throws Exception {

        Grid grid = createGrid(columns, rows, isCircular, cells);
        initializeSolver(grid);

        draw(solver.getGrid());

        nucleatingButton.setDisable(false);
        addInclusionsButton.setDisable(false);

        importFromFileButton.setDisable(false);
        importFromBitmapButton.setDisable(false);

        exportToFileButton.setDisable(false);
        exportToBitmapButton.setDisable(false);
    }

    private void initializeSolver(Grid grid) throws Exception {

        setOptionsToSolver(grid);
        solver.initialize();

        stepController.initialize();
        stepController.setSolver(solver);
        stepController.setOnSucceeded(
                event -> nextStep((Grid) event.getSource().getValue())
        );
        stepController.setPeriod(Duration.millis(DELAY));
    }

    private Grid createGrid(int columns, int rows, boolean isCircular, List<Cell> cells) {

        Grid grid;
        Growth growth = algorithmComboBox.getSelectionModel().getSelectedItem();

        if (cells != null) {
            if (Objects.equals(growth.toString(), "Monte Carlo grain growth")) {
                int numberOfGrains = Integer.parseInt(numberOfGrainsText.getText());
                grid = new Grid(columns, rows, isCircular, numberOfGrains, cells);
                startButton.setDisable(false);
            } else {
                grid = new Grid(columns, rows, isCircular, cells);
            }
        } else {
            if (Objects.equals(growth.toString(), "Monte Carlo grain growth")) {
                int numberOfGrains = Integer.parseInt(numberOfGrainsText.getText());
                grid = new Grid(columns, rows, isCircular, numberOfGrains);
                startButton.setDisable(false);
            } else {
                grid = new Grid(columns, rows, isCircular);
            }
        }

        int gridWidth = grid.getWidth();
        int gridHeight = grid.getHeight();

        int cellWidth = width / gridWidth;
        int cellHeight = height / gridHeight;

        if (cellWidth < cellHeight) {
            cellSize = cellWidth;
        } else {
            cellSize = cellHeight;
        }

        return grid;
    }

    @FXML
    public void start() throws Exception {

        solver.getGrowth().setProbability(Integer.parseInt(probabilityText.getText()));
        solver.getGrowth().setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyText.getText()));
        MCS = Integer.parseInt(MCSText.getText());

        if (stepController.getState() != Worker.State.READY) {
            stepController.restart();
        } else {
            stepController.start();
        }

        startButton.setDisable(true);
        stopButton.setDisable(false);
        nucleatingButton.setDisable(true);
    }

    @FXML
    private void stop() {

        stepController.cancel();

        startButton.setDisable(false);
        stopButton.setDisable(true);
        generateGridButton.setDisable(false);
    }

    @FXML
    public void nucleating() throws Exception {

        Nucleating nucleating = nucleatingComboBox.getSelectionModel().getSelectedItem();
        int numberOfGrains = Integer.parseInt(numberOfGrainsText.getText());

        solver.setNucleating(nucleating);
        solver.nucleating(numberOfGrains);

        draw(solver.getGrid());

        startButton.setDisable(false);
    }

    @FXML
    public void addInclusions() throws Exception {

        int amountOfInclusions = Integer.parseInt(amountOfInclusionsText.getText());
        int sizeOfInclusion = Integer.parseInt(sizeOfInclusionText.getText());
        int typeOfInclusion = typeOfInclusionComboBox.getSelectionModel().getSelectedIndex();

        solver.addInclusions(amountOfInclusions, sizeOfInclusion, typeOfInclusion);

        draw(solver.getGrid());
    }

    @FXML
    public void selectEdgeGrains() throws Exception {

        GrainSelection grainSelection = grainSelectionComboBox.getSelectionModel().getSelectedItem();
        solver.setGrainSelection(grainSelection);
        int numberOfGrains = Integer.parseInt(numberOfSelectedGrainsText.getText());

        List<Cell> cells = solver.selectEdgeGrains(numberOfGrains);
        generateGrid(solver.getGrid().getWidth(), solver.getGrid().getHeight(),
                cells, circularCheckBox.isSelected());
    }

    @FXML
    public void calculateEnergy() throws Exception {

        solver.getGrowth().setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyText.getText()));
        Growth growth = algorithmComboBox.getSelectionModel().getSelectedItem();
        Neighbourhood neighbourhood = neighbourhoodComboBox.getSelectionModel().getSelectedItem();
        EnergyDistribution energyDistribution = energyDistributionComboBox.getSelectionModel().getSelectedItem();
        Nucleating nucleating = nucleatingComboBox.getSelectionModel().getSelectedItem();
        NucleationModule nucleationModule = nucleationModuleComboBox.getSelectionModel().getSelectedItem();

        int energyInside = Integer.parseInt(energyInsideText.getText());
        int energyOnEdges = Integer.parseInt(energyOnEdgesText.getText());
        int numberOfGrains = Integer.parseInt(numberOfGrainsText.getText());

        nucleationModule.setProperties(numberOfGrains, nucleating);
        growth.setType(solver.getGrowth().getType());

        solver.setGrowth(growth);
        solver.setNeighbourhood(neighbourhood);
        solver.setEnergyDistribution(energyDistribution);
        solver.setNucleating(nucleating);
        solver.getGrowth().setNucleationModule(nucleationModule);

        solver.initialize();
        stepController.clearStep();

        solver.calculateEnergy(energyInside, energyOnEdges);
        nucleatingButton.setDisable(false);

        draw(solver.getGrid());
    }

    @FXML
    public void showEnergy() throws Exception {

        solver.getGrowth().setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyText.getText()));
        Growth growth = algorithmComboBox.getSelectionModel().getSelectedItem();
        EnergyDistribution energyDistribution = energyDistributionComboBox.getSelectionModel().getSelectedItem();
        int energyInside = Integer.parseInt(energyInsideText.getText());
        int energyOnEdges = Integer.parseInt(energyOnEdgesText.getText());

        solver.setGrowth(growth);
        solver.setEnergyDistribution(energyDistribution);
        solver.initialize();

        if (!showEnergyToggleButton.isSelected()) {
            solver.showMicrostructure(energyInside, energyOnEdges);
        } else {
            solver.showEnergy(energyInside, energyOnEdges);
        }

        nucleatingButton.setDisable(false);

        draw(solver.getGrid());
    }

    @FXML
    public void importFromFile() throws Exception {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import microstructure from file");
        chooser.setInitialDirectory(new File(MICROSTRUCTURES_FILES_DIR_PATH));
        File file = chooser.showOpenDialog(new Stage());

        String cellLine, gridDimensions, simulation;
        int gridWidth = 0, gridHeight = 0, type = 0;
        boolean isFinished = false, isCircular = false;
        List<Cell> cells = new ArrayList<>();

        if (file != null) {

            try (BufferedReader buffer = new BufferedReader(new FileReader(file.getPath()))) {

                simulation = buffer.readLine();
                isFinished = Boolean.parseBoolean(simulation.split(" ")[0]);
                type = Integer.parseInt(simulation.split(" ")[1]);

                gridDimensions = buffer.readLine();
                gridWidth = Integer.parseInt(gridDimensions.split(" ")[0]);
                gridHeight = Integer.parseInt(gridDimensions.split(" ")[1]);
                isCircular = Boolean.parseBoolean(gridDimensions.split(" ")[2]);

                while ((cellLine = buffer.readLine()) != null) {

                    String[] cell = cellLine.split(" ");
                    cells.add(new Cell(
                            Integer.parseInt(cell[0]),
                            Integer.parseInt(cell[1]),
                            Integer.parseInt(cell[2]),
                            Integer.parseInt(cell[3]),
                            Integer.parseInt(cell[4])
                    ));
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }

            generateGrid(gridWidth, gridHeight, cells, isCircular);
            solver.getGrowth().setType(type);
            solver.getGrowth().setFinished(isFinished);
            if (!isFinished) {
                startButton.setDisable(false);
            } else {
                selectGrainsButton.setDisable(false);
                showEnergyToggleButton.setDisable(false);
            }
        }
    }

    @FXML
    public void exportToFile() throws Exception {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export microstructure to file");
        chooser.setInitialDirectory(new File(MICROSTRUCTURES_FILES_DIR_PATH));
        chooser.setInitialFileName("microstructure.txt");

        File file = chooser.showSaveDialog(new Stage());

        if (file != null) {

            if (file.createNewFile()) {
                System.out.println("File is created");
            } else {
                System.out.println("File already exists");
            }

            Grid grid = solver.getGrid();

            String simulationData = stepController.prepareData();
            List<String> gridData = grid.prepareData();

            FileWriter writer = new FileWriter(file);

            writer.write(simulationData);
            writer.write(System.getProperty("line.separator"));

            for (String gridDataItem : gridData) {
                writer.write(gridDataItem);
                writer.write(System.getProperty("line.separator"));
            }

            writer.close();
        }
    }

    @FXML
    public void importFromBitmap() throws Exception {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import microstructure from bitmap");
        chooser.setInitialDirectory(new File(MICROSTRUCTURES_IMAGES_DIR_PATH));
        File file = chooser.showOpenDialog(new Stage());

        List<Cell> cells = new ArrayList<>();
        int type = circularCheckBox.isSelected() ? 1 : 0;

        if (file != null) {

            BufferedImage image = ImageIO.read(file);
            ColorGenerator.setState(Color.WHITE, 0);
            ColorGenerator.setState(Color.BLACK, -1);

            for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
                for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {

                    int color = image.getRGB(xPixel, yPixel);
                    cells.add(new Cell(
                            xPixel,
                            yPixel,
                            0,
                            ColorGenerator.getState(new Color(color)),
                            type
                    ));
                }
            }

            generateGrid(image.getWidth(), image.getHeight(), cells, circularCheckBox.isSelected());
            solver.getGrowth().setType(ColorGenerator.type);
            if (cells.stream().noneMatch(cell -> cell.getState() == 0)) {
                solver.getGrowth().setFinished(true);
                selectGrainsButton.setDisable(false);
                showEnergyToggleButton.setDisable(false);
            } else {
                startButton.setDisable(false);
            }
            exportToBitmapButton.setDisable(false);
        }
    }

    @FXML
    public void exportToBitmap() throws Exception {

        WritableImage exportedImage = new WritableImage(width, height);
        canvas.snapshot(null, exportedImage);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export microstructure to bitmap");
        fileChooser.setInitialDirectory(new File(MICROSTRUCTURES_IMAGES_DIR_PATH));
        fileChooser.setInitialFileName("microstructure.bmp");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {

            BufferedImage image = SwingFXUtils.fromFXImage(exportedImage, null);

            if (file.createNewFile()) {
                System.out.println("File is created");
            } else {
                System.out.println("File already exists");
            }

            // Export to bitmap
            BufferedImage bitmapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bitmapImage.getGraphics().drawImage(image, 0, 0, null);

            // Resize bitmap
            BufferedImage resized = ImageModifier.resize(bitmapImage, solver.getGrid().getWidth(), solver.getGrid().getHeight());

            ImageIO.write(resized, "bmp", file);

        }
    }

    private void setOptionsToSolver(Grid grid) {

        Growth growth = algorithmComboBox.getSelectionModel().getSelectedItem();
        Neighbourhood neighbourhood = neighbourhoodComboBox.getSelectionModel().getSelectedItem();
        GrainSelection grainSelection = grainSelectionComboBox.getSelectionModel().getSelectedItem();

        solver.setGrid(grid);
        solver.setGrowth(growth);
        solver.setNeighbourhood(neighbourhood);
        solver.setGrainSelection(grainSelection);
    }

    private void nextStep(Grid grid) {

        Platform.runLater(() -> {

            int step = stepController.getStep();

            if (MCS != 0 && step >= MCS) {

                stepController.clearStep();
                stop();
                structureComboBox.setDisable(false);

                int numberOfStates = Integer.parseInt(numberOfGrainsText.getText());
                solver.getGrowth().setType(numberOfStates);

                energyDistributionComboBox.setDisable(false);
                nucleationModuleComboBox.setDisable(false);
                calculateEnergyButton.setDisable(false);
                showEnergyToggleButton.setDisable(false);
            }

            if (grid.areAllCellsRecrystallized()) {
                stop();
            }

            if (stepController.isFinished()) {

                startButton.setDisable(true);
                stopButton.setDisable(true);
                generateGridButton.setDisable(false);
                structureComboBox.setDisable(false);
                grainSelectionComboBox.setDisable(false);
                selectGrainsButton.setDisable(false);
                energyDistributionComboBox.setDisable(false);
                nucleationModuleComboBox.setDisable(false);
                calculateEnergyButton.setDisable(false);
                showEnergyToggleButton.setDisable(false);
            }

            draw(grid);
        });
    }

    private void draw(Grid grid) {

        graphicsContext.clearRect(0, 0, width, height);

        if (grid.isCircular()) {
            drawCircularCells(grid);
        } else {
            drawSquareCells(grid);
        }
    }

    private void drawSquareCells(Grid grid) {
        grid.forEachCells(cell -> {
            graphicsContext.setFill(ColorGenerator.getColor(cell.getState(), cell.isRecrystallized()));
            graphicsContext.fillRect(
                    cell.getX() * cellSize,
                    cell.getY() * cellSize,
                    cellSize,
                    cellSize
            );
        });
    }

    private void drawCircularCells(Grid grid) {
        grid.forEachCells(cell -> {
            graphicsContext.setFill(ColorGenerator.getColor(cell.getState(), cell.isRecrystallized()));
            if (cell.getState() == Cell.INCLUSION_STATE && cell.getType() == Cell.SQUARE_TYPE) {
                graphicsContext.fillRect(
                        cell.getX() * cellSize,
                        cell.getY() * cellSize,
                        cellSize,
                        cellSize
                );
            } else {
                graphicsContext.fillOval(
                        cell.getX() * cellSize,
                        cell.getY() * cellSize,
                        cellSize,
                        cellSize
                );
            }
        });
    }
}
