package models.bo;

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

}
