import library.core.*;

class Vakje extends PComponent implements EventIgnorer {
    int i, j;
    float x, y;
    float w;
    int value;
    boolean valueGiven;

    Region region = null;

    boolean[] notes = new boolean[] { false, false, false, false, false };

    int highlightAlpha = 0;

    public Vakje(int i, int j, float w) {
        this.i = i;
        this.j = j;
        this.w = w;
        x = i * w;
        y = j * w;
        value = -1;
    }

    public void setValue(int value) {
        this.value = value;
        if (value != -1 && region != null) {
            for (Vakje vakje : region.region) {
                int index = value - 1;
                if (vakje.notes[index])
                    vakje.notes[index] = false;
            }
        }
    }

    public void setValueGiven(boolean given) {
        valueGiven = given;
    }

    public void editNotes(int note) {
        notes[note - 1] = !notes[note - 1];
    }

    public void draw() {
        // Draw notes
        if (value == -1) {
            fill(0, 120);
            textSize(w * 0.2);
            textAlign(CENTER);
            if (notes[0])
                text("1", x + w * 0.2, y + w * 0.2);
            if (notes[1])
                text("2", x + w * 0.8, y + w * 0.2);
            if (notes[2])
                text("3", x + w * 0.5, y + w * 0.5);
            if (notes[3])
                text("4", x + w * 0.2, y + w * 0.8);
            if (notes[4])
                text("5", x + w * 0.8, y + w * 0.8);

            return;
        }

        if (valueGiven) {
            fill(0);
        } else {
            fill(0, 150);
        }

        textSize(w * 0.7);
        textAlign(CENTER);
        text(value, x + w / 2, y + w / 2);

        if (highlightAlpha > 0) {
            fill(233, 255, 35, highlightAlpha);
            noStroke();
            rect(x, y, w, w);
            highlightAlpha = max(0, highlightAlpha - 5);
        }
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Vakje copy() {
        Vakje v = new Vakje(i, j, w);
        v.value = value;
        v.valueGiven = valueGiven;
        v.notes = notes.clone();
        return v;
    }

    public void setFromVakje(Vakje other) {
        i = other.i;
        j = other.j;
        x = other.x;
        y = other.y;
        w = other.w;
        value = other.value;
        valueGiven = other.valueGiven;
        notes = other.notes.clone();
    }

    @Override
    public String toString() {
        return "Vakje: " + i + ", " + j + " = " + value;
    }
}
