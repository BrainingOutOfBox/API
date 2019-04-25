package models.bo;

import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class NoteIdea extends Idea {

    public NoteIdea() {
        super();
    }

    public NoteIdea(String description) {
        super(description);
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
            throw new MarkdownSerializationException("Description or url is null");
        }
        return getPredecessor() + getDescription() + getSuccessor();
    }

}
