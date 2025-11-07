import java.util.*;
import library.core.*;

class Region extends PComponent implements EventIgnorer {
    int id;
    ArrayList<Vakje> region;
    color col;

    public Region(int id, Collection<Vakje> region) {
        this.id = id;
        this.region = new ArrayList<>(region);
        // col = color.randomColor();
        col = color.fromHSB(id / 8f, 0.75, 0.75);
    }

    public void draw() {
        for (Vakje vakje : region) {
            noStroke();

            fill(255);
            square(vakje.x, vakje.y, vakje.w);
            fill(col, 150);
            square(vakje.x, vakje.y, vakje.w);

            boolean[] edges = getEdges(vakje.i, vakje.j);
            strokeWeight(2);
            stroke(0, (edges[0]) ? 255 : 20);
            line(vakje.x, vakje.y, vakje.x, vakje.y + vakje.w);
            stroke(0, (edges[1]) ? 255 : 20);
            line(vakje.x + vakje.w, vakje.y, vakje.x + vakje.w, vakje.y + vakje.w);
            stroke(0, (edges[2]) ? 255 : 20);
            line(vakje.x, vakje.y, vakje.x + vakje.w, vakje.y);
            stroke(0, (edges[3]) ? 255 : 20);
            line(vakje.x, vakje.y + vakje.w, vakje.x + vakje.w, vakje.y + vakje.w);
        }
    }

    /**
     * Given a vakje, returns the edges that should be draw.
     * left, right, bottom, top
     */
    private boolean[] getEdges(int i, int j) {
        boolean[] edges = new boolean[] { true, true, true, true };

        // Left
        if (i > 0) {
            for (Vakje vakje : region) {
                if (vakje.i == i - 1 && vakje.j == j) {
                    edges[0] = false;
                }
            }
        }

        // Right
        if (i < Main.columns - 1) {
            for (Vakje vakje : region) {
                if (vakje.i == i + 1 && vakje.j == j) {
                    edges[1] = false;
                }
            }
        }

        // Top
        if (j > 0) {
            for (Vakje vakje : region) {
                if (vakje.i == i && vakje.j == j - 1) {
                    edges[2] = false;
                }
            }
        }

        // Bottom
        if (j < Main.rows - 1) {
            for (Vakje vakje : region) {
                if (vakje.i == i && vakje.j == j + 1) {
                    edges[3] = false;
                }
            }
        }

        return edges;
    }

    public boolean isSolved() {
        HashSet<Integer> values = new HashSet<>();
        for (Vakje vakje : region) {
            if (vakje.value == -1)
                return false;
            values.add(vakje.value);
        }

        return values.size() == region.size();
    }
}
