package models;

import java.util.ArrayList;

public final class Brainwave {
    private int nrOfBrainwave;
    ArrayList<Idea> ideas;

    public Brainwave() {
    }

    public int getNrOfBrainwave() {
        return nrOfBrainwave;
    }

    public void setNrOfBrainwave(int nrOfBrainwave) {
        this.nrOfBrainwave = nrOfBrainwave;
    }

    public ArrayList<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(ArrayList<Idea> ideas) {
        this.ideas = ideas;
    }
}
