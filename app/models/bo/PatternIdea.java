package models.bo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.image.Image;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.emphasis.ItalicText;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class PatternIdea extends Idea{

    private String problem;
    private String solution;
    private String url;
    private String category;
    private String pictureId;

    public PatternIdea() {
        super();
    }

    public PatternIdea(String description, String problem, String solution, String url, String category, String pictureId) {
        super(description);
        this.problem = problem;
        this.solution = solution;
        this.url = url;
        this.category = category;
        this.pictureId = pictureId;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        Config config = ConfigFactory.load();
        StringBuilder patternIdea = new StringBuilder();
        String text = getDescription();
        String url = config.getString("play.https.prodProtocol") + "://" + config.getString("play.https.prodAddress") + ":" + config.getString("play.https.prodPort") + "/Files/" + getPictureId() + "/download";

        patternIdea.append(new Image(text, url).toString()).append("\n")
                .append(new BoldText("Pattern:")).append(" ").append(new ItalicText(text).toString()).append("\n")
                .append(new BoldText("Problem:")).append(" ").append(new ItalicText(getProblem()).toString()).append("\n")
                .append(new BoldText("Solution:")).append(" ").append(new ItalicText(getSolution()).toString());

        return patternIdea.toString();
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        return "\n";
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {
        if (getDescription() == null || getPictureId() == null || getProblem() == null || getSolution() == null) {
            throw new MarkdownSerializationException("Description, problem, solution or picture is null");
        }

        return getPredecessor() +  getSuccessor();
    }
}
