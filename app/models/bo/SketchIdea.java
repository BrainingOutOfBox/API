package models.bo;

import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.image.Image;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class SketchIdea extends Idea {

    private String pictureId;

    public SketchIdea() {
        super();
    }


    public SketchIdea(String description, String pictureId) {
        super(description);
        this.pictureId = pictureId;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    @BsonIgnore
    @Override
    public String getPredecessor() {
        StringBuilder sketchIdea = new StringBuilder();
        String text = getDescription();
        String url = "http://localhost:40000/Files/" + getPictureId() + "/download";
        sketchIdea.append(new Image(text, url).toString());

        return sketchIdea.toString();
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        return "\n";
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {
        if (getDescription() == null || getPictureId() == null) {
            throw new MarkdownSerializationException("Description or pictureId is null");
        }

        return getPredecessor() + getSuccessor();
    }

}
