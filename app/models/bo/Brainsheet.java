package models.bo;

import java.util.ArrayList;

public final class Brainsheet {
    private int nrOfSheet;
    private ArrayList<Brainwave> brainwaves = new ArrayList<>();

    public Brainsheet() {

    }

    public Brainsheet(int nrOfSheet, ArrayList<Brainwave> brainwaves) {
        this.nrOfSheet = nrOfSheet;
        this.brainwaves = brainwaves;
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

    public void addBrainwave(Brainwave brainwave){
        this.brainwaves.add(brainwave);
    }
}
