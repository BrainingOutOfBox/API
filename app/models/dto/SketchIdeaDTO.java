package models.dto;

public class SketchIdeaDTO extends IdeaDTO {

    private String pictureId;

    public SketchIdeaDTO() {
        super();
    }

    public SketchIdeaDTO(String description, String pictureId) {
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
