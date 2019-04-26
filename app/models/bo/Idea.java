package models.bo;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownElement;

public abstract class Idea extends MarkdownElement implements MarkdownCascadable {
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
