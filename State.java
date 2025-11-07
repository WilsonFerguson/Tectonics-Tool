import java.util.*;
import library.core.*;

class State extends PComponent implements EventIgnorer {
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

    boolean solved = false;

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
            boolean drawRegion, HashSet<Vakje> drawingRegionOld, boolean settingValues, boolean solved) {
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
            Region region = new Region(regionOld.id, vakjes);
            regions.add(region);

            for (Vakje vakje : vakjes) {
                vakje.setRegion(region);
            }
        }

        this.drawRegion = drawRegion;
        drawingRegion = new HashSet<>(drawingRegionOld.size());
        for (Vakje vakje : drawingRegionOld) {
            drawingRegion.add(grid[vakje.i][vakje.j]);
        }

        this.settingValues = settingValues;
        this.solved = solved;
    }

    public State clone() {
        return new State(cols, rows, gridSize, grid, regions, drawRegion, drawingRegion, settingValues, solved);
    }

    public void checkSolved() {
        for (Region region : regions) {
            if (!region.isSolved()) {
                solved = false;
                return;
            }
        }

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int x = c + i;
                        int y = r + j;
                        if (x < 0 || x >= cols || y < 0 || y >= rows || (x == c && y == r))
                            continue;

                        if (grid[x][y].value == grid[c][r].value) {
                            solved = false;
                            return;
                        }
                    }
                }
            }
        }

        solved = true;
    }

    public void simplify(Sketch sketch) {
        while (true) {
            boolean changeMade = false;

            // Remove notes from adjacent vakjes or ones in same region
            for (int c = 0; c < cols; c++) {
                for (int r = 0; r < rows; r++) {
                    Vakje vakje = grid[c][r];
                    if (vakje.value != -1)
                        continue;
                    if (vakje.region != null) {
                        for (Vakje other : vakje.region.region) {
                            if (other.value != -1 && vakje.notes[other.value - 1]) {
                                vakje.notes[other.value - 1] = false;
                                changeMade = true;
                            }
                        }
                    }

                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int x = c + i;
                            int y = r + j;
                            if (x < 0 || x >= cols || y < 0 || y >= rows || (x == c && y == r))
                                continue;

                            if (grid[x][y].value != -1 && vakje.notes[grid[x][y].value - 1]) {
                                vakje.notes[grid[x][y].value - 1] = false;
                                changeMade = true;
                            }
                        }
                    }
                }
            }

            // If only one vakje in a region has a unique note, then assume that note
            for (Region region : regions) {
                HashMap<Integer, ArrayList<Vakje>> noteLocations = new HashMap<>();
                for (Vakje vakje : region.region) {
                    for (int i = 0; i < vakje.notes.length; i++) {
                        if (!vakje.notes[i])
                            continue;

                        if (noteLocations.containsKey(i)) {
                            noteLocations.get(i).add(vakje);
                        } else {
                            ArrayList<Vakje> list = new ArrayList<>();
                            list.add(vakje);
                            noteLocations.put(i, list);
                        }
                    }
                }

                for (Integer index : noteLocations.keySet()) {
                    if (noteLocations.get(index).size() == 1) {
                        noteLocations.get(index).get(0).setValue(index + 1);
                        changeMade = true;
                    }
                }
            }

            for (Region region : regions) {
                HashMap<Integer, ArrayList<Vakje>> noteLocations = new HashMap<>();
                for (Vakje vakje : region.region) {
                    for (int i = 0; i < vakje.notes.length; i++) {
                        if (vakje.notes[i]) {
                            if (noteLocations.containsKey(i)) {
                                noteLocations.get(i).add(vakje);
                            } else {
                                ArrayList<Vakje> list = new ArrayList<>();
                                list.add(vakje);
                                noteLocations.put(i, list);
                            }
                        }
                    }
                }

                for (Integer index : noteLocations.keySet()) {
                    ArrayList<Vakje> vakjes = noteLocations.get(index);

                    HashMap<PVector, Integer> counts = new HashMap<>();

                    // We go through all of the adjacent cells that are not in the same region and
                    // add that coordinate to the counts.
                    for (Vakje vakje : vakjes) {
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                int x = vakje.i + i;
                                int y = vakje.j + j;
                                if (x < 0 || x >= cols || y < 0 || y >= rows || (x == vakje.i && y == vakje.j))
                                    continue;

                                if (grid[x][y].region == null || grid[x][y].region == vakje.region)
                                    continue;

                                if (grid[x][y].notes[index]) {
                                    if (counts.containsKey(new PVector(x, y)))
                                        counts.put(new PVector(x, y), counts.get(new PVector(x, y)) + 1);
                                    else
                                        counts.put(new PVector(x, y), 1);
                                }
                            }
                        }
                    }

                    for (PVector p : counts.keySet()) {
                        if (counts.get(p) == vakjes.size())
                            grid[(int) p.x][(int) p.y].editNotes(index + 1);
                    }
                }
            }

            // Any vakjes with just 1 possible note should assume that value
            for (int c = 0; c < cols; c++) {
                for (int r = 0; r < rows; r++) {
                    if (grid[c][r].value != -1)
                        continue;

                    int noteValue = -1;
                    int noteCount = 0;
                    for (int i = 0; i < grid[c][r].notes.length; i++) {
                        if (grid[c][r].notes[i]) {
                            noteValue = i;
                            noteCount++;
                        }
                        if (noteCount > 1)
                            break;
                    }
                    if (noteCount == 1) {
                        grid[c][r].setValue(noteValue + 1);
                        changeMade = true;
                    }
                }
            }

            if (!changeMade)
                return;
        }
    }

    public void generateNotes() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Vakje vakje = grid[c][r];
                if (vakje.region == null) {
                    continue;
                }
                HashSet<Integer> values = new HashSet<>();
                for (int i = 0; i < vakje.region.region.size(); i++) {
                    values.add(i + 1);
                }
                for (Vakje other : vakje.region.region) {
                    if (vakje == other)
                        continue;
                    if (values.contains(other.value))
                        values.remove(other.value);
                }

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int x = c + i;
                        int y = r + j;
                        if (x < 0 || x >= cols || y < 0 || y >= rows || (x == c && y == r))
                            continue;

                        if (values.contains(grid[x][y].value))
                            values.remove(grid[x][y].value);
                    }
                }

                if (values.size() == 1)
                    vakje.setValue(values.iterator().next());

                for (Integer value : values) {
                    vakje.notes[value - 1] = true;
                }
            }
        }
    }
}
