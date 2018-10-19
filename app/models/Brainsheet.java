package models;

import java.util.ArrayList;

public final class Brainsheet {
    private int nrOfSheet;
    private ArrayList<Brainwave> brainwaves;

    public Brainsheet() {
    }

    public int getNrOfSheet() {
        return nrOfSheet;
    }

    public void setNrOfSheet(int nrOfSheet) {
        this.nrOfSheet = nrOfSheet;
    }

    public ArrayList<Brainwave> getBrainwaves() {
        return brainwaves;
    }

    public void setBrainwaves(ArrayList<Brainwave> brainwaves) {
        this.brainwaves = brainwaves;
    }
}
