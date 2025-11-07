import java.util.*;

class State {
    int cols, rows;
    int gridSize;

    /*
     * grid[column][row]
     */
    Vakje[][] grid;
    ArrayList<Region> regions = new ArrayList<>();

    boolean drawRegion = true;
    HashSet<Vakje> drawingRegion = new HashSet<>();

    boolean settingValues = true;

    Vakje firstMove;

    public State(int cols, int rows, int gridSize) {
        this.cols = cols;
        this.rows = rows;
        this.gridSize = gridSize;

        grid = new Vakje[cols][rows];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                grid[c][r] = new Vakje(c, r, gridSize);
            }
        }
    }

    public State(int cols, int rows, int gridSize, Vakje[][] gridOld, ArrayList<Region> regionsOld,
            boolean drawRegion, HashSet<Vakje> drawingRegionOld, boolean settingValues) {
        this.cols = cols;
        this.rows = rows;
        this.gridSize = gridSize;
        grid = new Vakje[cols][rows];
        for (int c = 0; c < gridOld.length; c++) {
            for (int r = 0; r < gridOld[c].length; r++) {
                grid[c][r] = gridOld[c][r].copy();
            }
        }

        regions = new ArrayList<>(regionsOld.size());
        for (Region regionOld : regionsOld) {
            ArrayList<Vakje> vakjes = new ArrayList<>();
            for (Vakje vakje : regionOld.region) {
                vakjes.add(grid[vakje.i][vakje.j]);
            }
            regions.add(new Region(regionOld.id, vakjes));
        }

        this.drawRegion = drawRegion;
        drawingRegion = new HashSet<>(drawingRegionOld.size());
        for (Vakje vakje : drawingRegionOld) {
            drawingRegion.add(grid[vakje.i][vakje.j]);
        }

        this.settingValues = settingValues;
    }

    public State clone() {
        return new State(cols, rows, gridSize, grid, regions, drawRegion, drawingRegion, settingValues);
    }
}
