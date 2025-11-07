import library.core.*;
import GameEngine.*;
import java.util.*;

public class Sketch extends Applet {

    Stack<State> history = new Stack<>();
    State state;

    public static final boolean SMART_FILL = true;

    public void setup() {
        int cols = Main.columns;
        int rows = Main.rows;
        float w = displayWidth * 0.8f / cols;
        float h = displayHeight * 0.8f / rows;

        int gridSize = round(min(w, h));

        size(cols * gridSize, rows * gridSize);

        state = new State(cols, rows, gridSize);
    }

    public void draw() {
        background(230);
        stroke(0, 100);
        strokeWeight(2);
        for (int c = 1; c < state.cols; c++) {
            line(c * state.gridSize, 0, c * state.gridSize, height);
        }
        for (int r = 1; r < state.rows; r++) {
            line(0, r * state.gridSize, width, r * state.gridSize);
        }

        Option<Vakje> hover = getMouseOver();
        if (state.drawRegion) {
            if (mousePressed && hover.isSome()) {
                if (!state.drawingRegion.contains(hover.unwrap())) {
                    state.drawingRegion.add(hover.unwrap());
                }
            }
        }

        for (Region region : state.regions) {
            region.draw();
        }
        for (int i = 0; i < state.grid.length; i++) {
            for (int j = 0; j < state.grid[i].length; j++) {
                state.grid[i][j].draw();
            }
        }

        if (hover.isSome()) {
            fill(0, 18);
            noStroke();
            square(hover.unwrap().x, hover.unwrap().y, state.gridSize);
        }

        if (state.drawRegion) {
            for (Vakje vakje : state.drawingRegion) {
                fill(0, 50);
                noStroke();
                square(vakje.x, vakje.y, state.gridSize);
            }
        }

        ArrayList<String> info = new ArrayList<>();
        String historyStr = "";
        for (int i = 0; i < history.size(); i++) {
            historyStr += history.get(i).firstMove != null ? "" + history.get(i).firstMove.value : "_";
        }
        info.add(historyStr + "_");
        if (state.solved)
            info.add("solved");
        if (state.drawRegion)
            info.add("region edit");
        if (state.settingValues)
            info.add("set values");
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
            state.drawRegion = !state.drawRegion;
            if (!state.drawRegion)
                state.drawingRegion.clear();
        } else if (key == 's') {
            state.settingValues = !state.settingValues;
            if (!state.settingValues && SMART_FILL) {
                state.generateNotes();
                state.simplify(this);
            }
        } else if (key == '.') {
            history.add(state);
            state = state.clone();
        } else if (key == ',') {
            if (!history.isEmpty()) {
                state = history.pop();
                if (state.firstMove != null) {
                    state.firstMove.highlightAlpha = 255;
                }
            }
        } else if (keyString.equals("Enter")) {
            if (state.drawRegion) {
                if (state.drawingRegion.size() > 0) {
                    history.add(state);
                    state = state.clone();

                    Region region = new Region(state.regions.size(), state.drawingRegion);
                    state.drawingRegion.clear();
                    state.regions.add(region);

                    for (Vakje vakje : region.region) {
                        vakje.setRegion(region);
                    }
                }
            }
        } else if (keyString.equals("Backspace")) {
            if (state.drawingRegion.size() > 0)
                state.drawingRegion.clear();
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
                    hover.unwrap().setValueGiven(state.settingValues);
                    if (state.firstMove == null)
                        state.firstMove = hover.unwrap();

                    state.checkSolved();
                    if (SMART_FILL)
                        state.simplify(this);
                }
            }
        }
    }

    public Option<Vakje> getMouseOver() {
        int i = mouseX / state.gridSize;
        int j = mouseY / state.gridSize;
        Vakje vakje = (i < 0 || i >= state.cols || j < 0 || j >= state.rows) ? null : state.grid[i][j];
        return new Option<>(vakje);
    }
}
