package models.md;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownElement;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;

public final class Brainwave extends MarkdownElement implements MarkdownCascadable {
    private int nrOfBrainwave;
    ArrayList<Idea> ideas = new ArrayList<>();

    public Brainwave() {

    }

    public Brainwave(int nrOfBrainwave, ArrayList<Idea> ideas) {
        this.nrOfBrainwave = nrOfBrainwave;
        this.ideas = ideas;
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

    public void addIdea(Idea idea){
        this.ideas.add(idea);
    }

    @BsonIgnore
    @Override
    public String getPredecessor() {
        return new Heading("Brainwave ", 4).toString();
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        return "\n";
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {
        if (nrOfBrainwave == -1 || getIdeas() == null) {
            throw new MarkdownSerializationException("nrOfBrainwave is null or ideas");
        }

        StringBuilder ideas = new StringBuilder();

        for (Idea idea: getIdeas()){
            ideas.append(idea.serialize());
        }
        return getPredecessor() + getNrOfBrainwave() + getSuccessor() + ideas.toString();
    }
}
