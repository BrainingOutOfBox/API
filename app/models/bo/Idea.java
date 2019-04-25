package models.bo;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownElement;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public abstract class Idea extends MarkdownElement implements MarkdownCascadable {
    private String description;

    public Idea() {

    }

    public Idea(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @BsonIgnore
    @Override
    public String getPredecessor() {
        return "- ";
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        return "\n";
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {
        if (getDescription() == null) {
            throw new MarkdownSerializationException("Description is null");
        }
        return getPredecessor() + getDescription() + getSuccessor();
    }
}
