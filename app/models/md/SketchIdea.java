package models.md;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
        Config config =  ConfigFactory.load();
        StringBuilder sketchIdea = new StringBuilder();
        String text = getDescription();
        String url = config.getString("play.https.prodProtocol") + "://" + config.getString("play.https.prodAddress") + ":" + config.getString("play.https.prodPort") + "/Files/" + getPictureId() + "/download";
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
