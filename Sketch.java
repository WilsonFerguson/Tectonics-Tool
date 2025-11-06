import library.core.*;
import GameEngine.*;
import java.util.*;

public class Sketch extends Applet {

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
    Vakje[][] gridTrialSave;

    public void setup() {
        columns = Main.columns;
        rows = Main.rows;

        float w = displayWidth * 0.8f / columns;
        float h = displayHeight * 0.8f / rows;

        gridSize = round(min(w, h));

        size(columns * gridSize, rows * gridSize);

        grid = new Vakje[columns][rows];
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                grid[c][r] = new Vakje(c, r, gridSize);
            }
        }

        gridTrialSave = new Vakje[columns][rows];
    }

    public void draw() {
        background(230);
        stroke(0, 100);
        strokeWeight(2);
        for (int c = 1; c < columns; c++) {
            line(c * gridSize, 0, c * gridSize, height);
        }
        for (int r = 1; r < rows; r++) {
            line(0, r * gridSize, width, r * gridSize);
        }

        Option<Vakje> hover = getMouseOver();
        if (drawRegion) {
            if (mousePressed && hover.isSome()) {
                if (!drawingRegion.contains(hover.unwrap())) {
                    drawingRegion.add(hover.unwrap());
                }
            }
        }

        for (Region region : regions) {
            region.draw();
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].draw();
            }
        }

        if (hover.isSome()) {
            fill(0, 18);
            noStroke();
            square(hover.unwrap().x, hover.unwrap().y, gridSize);
        }

        if (drawRegion) {
            for (Vakje vakje : drawingRegion) {
                fill(0, 50);
                noStroke();
                square(vakje.x, vakje.y, gridSize);
            }
        }

        ArrayList<String> info = new ArrayList<>();
        if (drawRegion)
            info.add("region edit");
        if (settingValues)
            info.add("set values");
        if (trialMode)
            info.add("trial");
        fill(0);
        textAlign(RIGHT);
        textSize(20);
        for (int i = 0; i < info.size(); i++) {
            float y = textAscent() * 1.1f + (i * (textAscent() + textDescent()));
            text(info.get(i), width - 10, y);
        }
    }

    public void keyTyped() {
        // Region
        if (key == 'r') {
            drawRegion = !drawRegion;
            if (!drawRegion)
                drawingRegion.clear();
        } else if (key == 's') {
            settingValues = !settingValues;
        } else if (key == 't') {
            trialMode = !trialMode;
            if (trialMode) {
                for (int c = 0; c < columns; c++) {
                    for (int r = 0; r < rows; r++) {
                        gridTrialSave[c][r] = grid[c][r].copy();
                    }
                }
            } else {
                for (int c = 0; c < columns; c++) {
                    for (int r = 0; r < rows; r++) {
                        grid[c][r].setFromVakje(gridTrialSave[c][r]);
                    }
                }
            }
        } else if (keyString.equals("Enter")) {
            if (drawRegion) {
                if (drawingRegion.size() > 0) {
                    Region region = new Region(regions.size(), drawingRegion);
                    drawingRegion.clear();
                    regions.add(region);

                    for (Vakje vakje : region.region) {
                        vakje.setRegion(region);
                    }
                }
            }
        } else if (keyString.equals("Backspace")) {
            if (drawingRegion.size() > 0)
                drawingRegion.clear();
            else {
                Option<Vakje> hover = getMouseOver();
                if (hover.isSome()) {
                    hover.unwrap().setValue(-1);
                }
            }
        }

        if (parseChar(keyString) >= '1' && parseChar(keyString) <= '5') {
            Option<Vakje> hover = getMouseOver();
            if (hover.isSome()) {
                if (keysPressed.contains("Shift")) {
                    hover.unwrap().editNotes(parseChar(keyString) - '0');
                } else {
                    hover.unwrap().setValue(parseInt(keyString));
                    hover.unwrap().setValueGiven(settingValues);
                }
            }
        }
    }

    public Option<Vakje> getMouseOver() {
        // int i = max(min(mouseX / gridSize, columns - 1), 0);
        // int j = max(min(mouseY / gridSize, rows - 1), 0);
        // return grid[i][j];
        int i = mouseX / gridSize;
        int j = mouseY / gridSize;
        Vakje vakje = (i < 0 || i >= columns || j < 0 || j >= rows) ? null : grid[i][j];
        return new Option<>(vakje);
    }

    // public void saveState() {
    // StringBuilder sb = new StringBuilder();
    // // Cols, rows, gridSize
    // sb.append(columns + " " + rows + " " + gridSize + "\n");
    //
    // // Grid, regions
    // for (int i = 0; i < columns; i++) {
    // for (int j = 0; j < rows; j++) {
    // sb.append(grid[i][j].value + " ");
    // }
    // sb.append("\n");
    // }
    //
    // // trialMode, gridTrialSave
    // }
}
