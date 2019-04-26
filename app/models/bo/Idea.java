package models.bo;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public abstract class Idea implements MarkdownCascadable {
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
    public String serialize() throws MarkdownSerializationException {
        return "";
    }

}
