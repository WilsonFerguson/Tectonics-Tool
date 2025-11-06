import java.util.*;

class State {
    int columns, rows;
    int gridSize;

    /*
     * grid[column][row]
     */
    Vakje[][] grid;
    ArrayList<Region> regions = new ArrayList<>();

    boolean drawRegion = true;
    HashSet<Vakje> drawingRegion = new HashSet<>();

    boolean settingValues = true;
    boolean trialMode = false;

    public State(int columns, int rows, int gridSize, Vakje[][] gridOld, ArrayList<Region> regionsOld,
            boolean drawRegion, HashSet<Vakje> drawingRegionOld, boolean settingValues, boolean trialMode) {
        this.columns = columns;
        this.rows = rows;
        this.gridSize = gridSize;
        grid = new Vakje[columns][rows];
        for (int c = 0; c < gridOld.length; c++) {
            for (int r = 0; r < gridOld[c].length; r++) {
                grid[c][r] = gridOld[c][r];
            }
        }

        regions = new ArrayList<>(regionsOld);

        this.drawRegion = drawRegion;
        drawingRegion = new HashSet<>(drawingRegionOld);

        this.settingValues = settingValues;
        this.trialMode = trialMode;
    }
}
