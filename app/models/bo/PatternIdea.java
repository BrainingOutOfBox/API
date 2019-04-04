package models.bo;

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
}
