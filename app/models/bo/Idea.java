package models.bo;

public final class Idea {
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
}
