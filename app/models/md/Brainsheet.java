package models.md;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownElement;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;

public final class Brainsheet extends MarkdownElement implements MarkdownCascadable {
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

    @BsonIgnore
    @Override
    public String getPredecessor() {
        return new Heading("Brainsheet ",3).toString();
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        return "\n";
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {
        if (nrOfSheet == -1 || getBrainwaves() == null) {
            throw new MarkdownSerializationException("nrOfSheet is null or brainwaves");
        }

        StringBuilder brainwaves = new StringBuilder();

        for (Brainwave brainwave: getBrainwaves()){
            brainwaves.append(brainwave.serialize());
        }
        return getPredecessor() + getNrOfSheet() + getSuccessor() + brainwaves.toString();
    }
}
