package models.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoteIdeaDTO.class, name = "noteIdea"),
        @JsonSubTypes.Type(value = SketchIdeaDTO.class, name = "sketchIdea"),
        @JsonSubTypes.Type(value = PatternIdeaDTO.class, name = "patternIdea")
})
public abstract class IdeaDTO {
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
