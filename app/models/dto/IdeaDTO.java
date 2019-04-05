package models.dto;

public final class IdeaDTO {
    private String description;

    public IdeaDTO() {

    }

    public IdeaDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
